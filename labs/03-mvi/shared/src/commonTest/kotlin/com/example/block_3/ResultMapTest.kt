package com.example.block_3

import com.example.block_3.core.Result
import com.example.block_3.core.map
import com.example.block_3.data.posts.model.PostDto
import com.example.block_3.data.posts.model.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ResultMapTest {

    @Test
    fun mapTransformsSuccessData() {
        val dto = PostDto(id = 1, userId = 7, title = "Title", body = "Body")
        val result: Result<PostDto> = Result.Success(dto)

        val mapped = result.map { it.toDomain() }

        assertEquals(Result.Success(dto.toDomain()), mapped)
    }

    @Test
    fun mapKeepsFailureUnchanged() {
        val failure: Result<PostDto> = Result.Failure("Network error")
        var called = false

        val mapped = failure.map { called = true; it.toDomain() }

        assertSame<Result<*>>(failure, mapped)
        assertEquals(false, called)
    }
}
