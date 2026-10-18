package com.example.block_3.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.block_3.core.onFailure
import com.example.block_3.core.onSuccess
import com.example.block_3.domain.posts.create.CreatePostUseCase
import com.example.block_3.domain.posts.edit.EditPostUseCase
import com.example.block_3.domain.posts.obtain.ObtainPostUseCase
import com.example.block_3.domain.posts.remove.RemovePostUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val obtainPostUseCase: ObtainPostUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val editPostUseCase: EditPostUseCase,
    private val removePostUseCase: RemovePostUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _events = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val events: Flow<MainEvent> = _events.receiveAsFlow()

    // Single entry point from the UI
    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.OnTitleChange -> _state.update { it.copy(titleInput = action.title) }
            is MainAction.OnBodyChange -> _state.update { it.copy(bodyInput = action.body) }
            MainAction.OnGetClick -> obtainPost()
            MainAction.OnPostClick -> createPost()
            MainAction.OnPutClick -> updatePost()
            MainAction.OnDeleteClick -> deletePost()
        }
    }

    private fun obtainPost() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            obtainPostUseCase(DEFAULT_POST_ID)
                .onSuccess { post ->
                    _state.update {
                        it.copy(
                            post = post,
                            titleInput = post.title,
                            bodyInput = post.body,
                            lastOperation = "GET",
                        )
                    }
                }
                .onFailure { message -> _events.send(MainEvent.ShowError(message)) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun createPost() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val current = _state.value
            createPostUseCase(current.titleInput, current.bodyInput)
                .onSuccess { post -> _state.update { it.copy(post = post, lastOperation = "POST") } }
                .onFailure { message -> _events.send(MainEvent.ShowError(message)) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun updatePost() {
        val current = _state.value
        val post = current.post
        if (post == null) {
            _events.trySend(MainEvent.ShowError("Load a post first (GET)"))
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            editPostUseCase(post, current.titleInput, current.bodyInput)
                .onSuccess { updated -> _state.update { it.copy(post = updated, lastOperation = "PUT") } }
                .onFailure { message -> _events.send(MainEvent.ShowError(message)) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun deletePost() {
        val postId = _state.value.post?.id ?: DEFAULT_POST_ID
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            removePostUseCase(postId)
                .onSuccess { _state.update { it.copy(post = null, lastOperation = "DELETE #$postId") } }
                .onFailure { message -> _events.send(MainEvent.ShowError(message)) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private companion object {
        const val DEFAULT_POST_ID = 1
    }
}
