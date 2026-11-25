package ua.chnu.campustix.presentation.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.domain.posts.GetPostsPageUseCase
import ua.chnu.campustix.domain.posts.Post

// Key = how many posts to skip (dummyjson uses limit/skip), Value = a post
class PostsPagingSource(
    private val getPostsPageUseCase: GetPostsPageUseCase,
) : PagingSource<Int, Post>() {

    private val log = Logger.withTag("PostsPaging")

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        // null key = the first load or a refresh: start from the beginning
        val skip = params.key ?: FIRST_SKIP
        log.d { "Load: skip=$skip, loadSize=${params.loadSize}" }

        return when (val result = getPostsPageUseCase(limit = params.loadSize, skip = skip)) {
            is Result.Success -> {
                val page = result.data
                val nextSkip = page.skip + page.posts.size
                LoadResult.Page(
                    data = page.posts,
                    // the list only grows down, there is nothing before the first page
                    prevKey = null,
                    // null tells Paging that the end of the list is reached
                    nextKey = if (page.posts.isEmpty() || nextSkip >= page.total) null else nextSkip,
                )
            }
            is Result.Failure -> {
                log.w { "Load failed: ${result.error}" }
                LoadResult.Error(DataErrorException(result.error))
            }
        }
    }

    // After refresh() the feed loads again from the first page
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? = null

    companion object {
        const val FIRST_SKIP = 0
    }
}

// LoadResult.Error needs a Throwable; the DataError inside lets the UI pick the right text
class DataErrorException(val error: DataError) : Exception(error.name)
