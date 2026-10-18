package com.example.block_3.domain.posts.remove

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.PostRepository

class RemovePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Int): Result<Int> {
        return postRepository.deletePost(postId)
    }
}
