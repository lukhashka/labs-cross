package com.example.block_3

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.create.CreatePostUseCase
import com.example.block_3.domain.posts.edit.EditPostUseCase
import com.example.block_3.domain.posts.model.NewPost
import com.example.block_3.domain.posts.model.Post
import com.example.block_3.domain.posts.obtain.ObtainPostUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

// Fake repository: remembers what was sent, no network.
private class FakePostRepository : PostRepository {
    var createdPost: NewPost? = null

    override suspend fun getPost(postId: Int): Result<Post> =
        Result.Success(Post(postId, 1, "Title", "Body"))

    override suspend fun createPost(post: NewPost): Result<Post> {
        createdPost = post
        return Result.Success(Post(101, post.authorId, post.title, post.body))
    }

    override suspend fun updatePost(post: Post): Result<Post> = Result.Success(post)

    override suspend fun deletePost(postId: Int): Result<Int> = Result.Success(postId)
}

class PostUseCasesTest {

    @Test
    fun createPostWithBlankTitleFailsWithoutCallingRepository() = runTest {
        val repository = FakePostRepository()
        val createPost = CreatePostUseCase(repository)

        val result = createPost("   ", "Body")

        assertIs<Result.Failure>(result)
        assertNull(repository.createdPost)
    }

    @Test
    fun createPostTrimsText() = runTest {
        val repository = FakePostRepository()
        val createPost = CreatePostUseCase(repository)

        createPost("  Hello  ", " World ")

        assertEquals("Hello", repository.createdPost?.title)
        assertEquals("World", repository.createdPost?.body)
    }

    @Test
    fun editPostReplacesTitleAndBody() = runTest {
        val editPost = EditPostUseCase(FakePostRepository())
        val post = Post(1, 1, "Old", "Old body")

        val result = editPost(post, "New", "New body")

        assertEquals(Result.Success(Post(1, 1, "New", "New body")), result)
    }

    @Test
    fun obtainPostRejectsNonPositiveId() = runTest {
        val obtainPost = ObtainPostUseCase(FakePostRepository())

        assertIs<Result.Failure>(obtainPost(0))
    }
}
