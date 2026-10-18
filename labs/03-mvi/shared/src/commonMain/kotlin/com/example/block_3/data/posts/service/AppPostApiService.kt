package com.example.block_3.data.posts.service

import com.example.block_3.core.Result
import com.example.block_3.data.common.safeRequest
import com.example.block_3.data.posts.model.NewPostDto
import com.example.block_3.data.posts.model.PostDto
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AppPostApiService(
    private val client: HttpClient
) : PostApiService {

    override suspend fun getPost(postId: Int): Result<PostDto> {
        return client.safeRequest {
            get("$BASE_URL/$POSTS_API/$postId") {
                accept(ContentType.Application.Json)
            }
        }
    }

    override suspend fun addPost(post: NewPostDto): Result<PostDto> {
        return client.safeRequest {
            post("$BASE_URL/$POSTS_API") {
                contentType(ContentType.Application.Json)
                setBody(post)
            }
        }
    }

    override suspend fun updatePost(post: PostDto): Result<PostDto> {
        return client.safeRequest {
            put("$BASE_URL/$POSTS_API/${post.id}") {
                contentType(ContentType.Application.Json)
                setBody(post)
            }
        }
    }

    override suspend fun deletePost(postId: Int): Result<Unit> {
        return client.safeRequest {
            delete("$BASE_URL/$POSTS_API/$postId")
        }
    }
}
