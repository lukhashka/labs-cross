package com.example.block_3.data.posts

import com.example.block_3.core.Result
import com.example.block_3.core.map
import com.example.block_3.data.posts.model.toDomain
import com.example.block_3.data.posts.model.toDto
import com.example.block_3.data.posts.service.PostApiService
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.model.NewPost
import com.example.block_3.domain.posts.model.Post

// Repository works with DTO from the API and returns domain models.
// `map` converts PostDto -> Post only when the request succeeded.
class AppPostRepository(
    private val postApiService: PostApiService
) : PostRepository {

    override suspend fun getPost(postId: Int): Result<Post> {
        return postApiService.getPost(postId).map { it.toDomain() }
    }

    override suspend fun createPost(post: NewPost): Result<Post> {
        return postApiService.addPost(post.toDto()).map { it.toDomain() }
    }

    override suspend fun updatePost(post: Post): Result<Post> {
        return postApiService.updatePost(post.toDto()).map { it.toDomain() }
    }

    override suspend fun deletePost(postId: Int): Result<Int> {
        // API returns an empty object, so we return id of the deleted post instead
        return postApiService.deletePost(postId).map { postId }
    }
}
