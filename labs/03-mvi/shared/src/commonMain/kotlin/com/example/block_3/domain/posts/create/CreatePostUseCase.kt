package com.example.block_3.domain.posts.create

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.model.NewPost
import com.example.block_3.domain.posts.model.Post

class CreatePostUseCase(
    private val postRepository: PostRepository
) {
    // Business rule: a post must have a title; text is trimmed before sending.
    suspend operator fun invoke(title: String, body: String): Result<Post> {
        if (title.isBlank()) {
            return Result.Failure("Title must not be empty")
        }
        val post = NewPost(
            authorId = DEFAULT_AUTHOR_ID,
            title = title.trim(),
            body = body.trim(),
        )
        return postRepository.createPost(post)
    }

    private companion object {
        const val DEFAULT_AUTHOR_ID = 42
    }
}
