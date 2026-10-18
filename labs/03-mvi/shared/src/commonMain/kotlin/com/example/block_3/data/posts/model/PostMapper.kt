package com.example.block_3.data.posts.model

import com.example.block_3.domain.posts.model.NewPost
import com.example.block_3.domain.posts.model.Post

fun PostDto.toDomain(): Post = Post(
    id = id,
    authorId = userId,
    title = title,
    body = body,
)

fun Post.toDto(): PostDto = PostDto(
    id = id,
    userId = authorId,
    title = title,
    body = body,
)

fun NewPost.toDto(): NewPostDto = NewPostDto(
    userId = authorId,
    title = title,
    body = body,
)
