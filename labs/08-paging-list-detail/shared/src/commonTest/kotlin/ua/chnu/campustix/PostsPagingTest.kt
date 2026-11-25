package ua.chnu.campustix

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.AuthSession
import ua.chnu.campustix.data.auth.service.AppAuthApiService
import ua.chnu.campustix.data.network.createAuthorizedHttpClient
import ua.chnu.campustix.data.network.createPlainHttpClient
import ua.chnu.campustix.data.posts.AppPostRepository
import ua.chnu.campustix.data.posts.service.AppPostApiService
import ua.chnu.campustix.domain.posts.GetPostsPageUseCase
import ua.chnu.campustix.domain.posts.Post
import ua.chnu.campustix.domain.posts.PostRepository
import ua.chnu.campustix.domain.posts.PostsPage
import ua.chnu.campustix.presentation.home.paging.DataErrorException
import ua.chnu.campustix.presentation.home.paging.PostsPagingSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

// 23 posts on the "server"; the fake answers like dummyjson: limit/skip -> a slice and the total
private class FakePostRepository(
    private val total: Int = 23,
    private val error: DataError? = null,
) : PostRepository {
    val requests = mutableListOf<Pair<Int, Int>>()

    override suspend fun getPostsPage(limit: Int, skip: Int): Result<PostsPage> {
        requests += limit to skip
        if (error != null) return Result.Failure(error)
        val posts = (skip until minOf(skip + limit, total)).map { post(it + 1) }
        return Result.Success(PostsPage(posts = posts, skip = skip, total = total))
    }
}

private fun post(id: Int) = Post(
    id = id, title = "Post $id", body = "Body", tags = listOf("campus"),
    likes = 1, dislikes = 0, views = 10, authorId = 7,
)

class PostsPagingTest {

    private fun refresh(loadSize: Int) =
        PagingSource.LoadParams.Refresh<Int>(key = null, loadSize = loadSize, placeholdersEnabled = false)

    private fun append(key: Int, loadSize: Int) =
        PagingSource.LoadParams.Append(key = key, loadSize = loadSize, placeholdersEnabled = false)

    @Test
    fun firstLoadStartsFromZeroAndPointsToNextPage() = runTest {
        val repository = FakePostRepository()
        val source = PostsPagingSource(GetPostsPageUseCase(repository))

        val page = assertIs<PagingSource.LoadResult.Page<Int, Post>>(source.load(refresh(10)))

        assertEquals(10 to 0, repository.requests.single())
        assertEquals((1..10).toList(), page.data.map { it.id })
        assertNull(page.prevKey)
        assertEquals(10, page.nextKey)
    }

    @Test
    fun lastPageHasNoNextKey() = runTest {
        val source = PostsPagingSource(GetPostsPageUseCase(FakePostRepository()))

        val page = assertIs<PagingSource.LoadResult.Page<Int, Post>>(source.load(append(key = 20, loadSize = 10)))

        assertEquals(listOf(21, 22, 23), page.data.map { it.id })
        assertNull(page.nextKey)
    }

    @Test
    fun emptyPageEndsTheList() = runTest {
        val source = PostsPagingSource(GetPostsPageUseCase(FakePostRepository(total = 0)))

        val page = assertIs<PagingSource.LoadResult.Page<Int, Post>>(source.load(refresh(10)))

        assertEquals(emptyList(), page.data)
        assertNull(page.nextKey)
    }

    @Test
    fun failureBecomesLoadResultError() = runTest {
        val source = PostsPagingSource(GetPostsPageUseCase(FakePostRepository(error = DataError.NoInternet)))

        val result = assertIs<PagingSource.LoadResult.Error<Int, Post>>(source.load(refresh(10)))

        assertEquals(DataError.NoInternet, assertIs<DataErrorException>(result.throwable).error)
    }

    @Test
    fun refreshKeyIsNullSoRefreshStartsFromFirstPage() {
        val source = PostsPagingSource(GetPostsPageUseCase(FakePostRepository()))
        val state = PagingState<Int, Post>(
            pages = emptyList(), anchorPosition = 5,
            config = PagingConfig(pageSize = 10), leadingPlaceholderCount = 0,
        )
        assertNull(source.getRefreshKey(state))
    }

    @Test
    fun repositoryRequestsOnePageWithTokenAndMapsIt() = runTest {
        val storage = FakeSecureStorage(access = "access", refresh = "refresh")
        val engine = MockEngine { request ->
            assertEquals("/auth/posts", request.url.encodedPath)
            assertEquals("15", request.url.parameters["limit"])
            assertEquals("30", request.url.parameters["skip"])
            assertEquals("Bearer access", request.headers[HttpHeaders.Authorization])
            respond(
                """{"posts":[{"id":31,"title":"T","body":"B","tags":["a","b"],
                   "reactions":{"likes":5,"dislikes":1},"views":9,"userId":4}],
                   "total":251,"skip":30,"limit":15}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val authApi = AppAuthApiService(createPlainHttpClient(engine))
        val client = createAuthorizedHttpClient(engine, storage, authApi, AuthSession())
        val repository = AppPostRepository(AppPostApiService(client))

        val page = assertIs<Result.Success<PostsPage>>(repository.getPostsPage(limit = 15, skip = 30)).data

        assertEquals(251, page.total)
        assertEquals(30, page.skip)
        assertEquals(
            Post(id = 31, title = "T", body = "B", tags = listOf("a", "b"), likes = 5, dislikes = 1, views = 9, authorId = 4),
            page.posts.single(),
        )
    }
}
