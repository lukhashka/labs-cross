package com.example.block_3.data.posts.service

import com.example.block_3.core.Result
import com.example.block_3.data.posts.model.NewPostDto
import com.example.block_3.data.posts.model.PostDto

const val BASE_URL = "https://jsonplaceholder.typicode.com"
const val POSTS_API = "posts"

interface PostApiService {
    suspend fun getPost(postId: Int): Result<PostDto>
    suspend fun addPost(post: NewPostDto): Result<PostDto>
    suspend fun updatePost(post: PostDto): Result<PostDto>
    suspend fun deletePost(postId: Int): Result<Unit>
}
