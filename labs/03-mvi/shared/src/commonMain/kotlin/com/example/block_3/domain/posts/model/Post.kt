package com.example.block_3.domain.posts.model

// Domain models: plain Kotlin, no Ktor / serialization annotations.
data class Post(
    val id: Int,
    val authorId: Int,
    val title: String,
    val body: String,
)

data class NewPost(
    val authorId: Int,
    val title: String,
    val body: String,
)
