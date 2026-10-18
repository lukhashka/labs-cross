package com.example.block_3.domain.posts

import com.example.block_3.core.Result
import com.example.block_3.domain.posts.model.NewPost
import com.example.block_3.domain.posts.model.Post

// Domain declares what it needs, data layer implements it (AppPostRepository).
interface PostRepository {
    suspend fun getPost(postId: Int): Result<Post>
    suspend fun createPost(post: NewPost): Result<Post>
    suspend fun updatePost(post: Post): Result<Post>
    suspend fun deletePost(postId: Int): Result<Int>
}
