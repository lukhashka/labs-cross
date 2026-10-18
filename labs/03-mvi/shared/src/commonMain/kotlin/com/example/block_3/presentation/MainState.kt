package com.example.block_3.presentation

import androidx.compose.runtime.Immutable
import com.example.block_3.domain.posts.model.Post

// The whole screen in one immutable snapshot. UI only draws it.
@Immutable
data class MainState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val titleInput: String = "",
    val bodyInput: String = "",
    val lastOperation: String? = null,
)
