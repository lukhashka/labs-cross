package com.example.block_3.domain.posts.obtain

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.model.Post

class ObtainPostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Int): Result<Post> {
        if (postId <= 0) {
            return Result.Failure("Post id must be positive")
        }
        return postRepository.getPost(postId)
    }
}
