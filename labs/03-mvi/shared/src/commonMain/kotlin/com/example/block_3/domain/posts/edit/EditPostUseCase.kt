package com.example.block_3.domain.posts.edit

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.model.Post

class EditPostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(post: Post, newTitle: String, newBody: String): Result<Post> {
        if (newTitle.isBlank()) {
            return Result.Failure("Title must not be empty")
        }
        val updated = post.copy(title = newTitle.trim(), body = newBody.trim())
        return postRepository.updatePost(updated)
    }
}
