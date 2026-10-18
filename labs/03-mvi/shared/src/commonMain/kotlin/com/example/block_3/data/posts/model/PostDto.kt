package com.example.block_3.data.posts.model

import kotlinx.serialization.Serializable

// JSON shape of jsonplaceholder.typicode.com/posts
@Serializable
data class PostDto(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val body: String,
)

@Serializable
data class NewPostDto(
    val userId: Int,
    val title: String,
    val body: String,
)
