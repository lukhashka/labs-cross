package ua.chnu.campustix.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.onFailure
import ua.chnu.campustix.core.onSuccess
import ua.chnu.campustix.domain.auth.LogoutUseCase
import ua.chnu.campustix.domain.posts.GetPostsPageUseCase
import ua.chnu.campustix.domain.posts.Post
import ua.chnu.campustix.domain.user.GetCurrentUserUseCase
import ua.chnu.campustix.presentation.home.paging.PostsPagingSource

const val POSTS_PAGE_SIZE = 15

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPostsPageUseCase: GetPostsPageUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    // The feed is a separate stream: Paging keeps the loaded pages and their load states itself.
    // cachedIn keeps the pages in the ViewModel, so after rotation the list is not loaded again.
    val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = POSTS_PAGE_SIZE,
            initialLoadSize = POSTS_PAGE_SIZE,
            enablePlaceholders = false,
        ),
        // refresh() needs a new PagingSource every time, so this is a factory
        pagingSourceFactory = { PostsPagingSource(getPostsPageUseCase) },
    ).flow.cachedIn(viewModelScope)

    init {
        loadUser()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnSettingsClick -> _events.trySend(HomeEvent.NavigateToSettings)
            HomeAction.OnLogoutClick -> logout()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { user -> _state.update { it.copy(user = user) } }
                .onFailure { error ->
                    // Unauthorized here means the session has ended: MainViewModel moves to sign in
                    if (error != DataError.Unauthorized) {
                        _events.send(HomeEvent.ShowError(error))
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.send(HomeEvent.NavigateToAuth)
        }
    }
}
