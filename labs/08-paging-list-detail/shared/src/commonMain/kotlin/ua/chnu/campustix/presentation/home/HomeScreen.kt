package ua.chnu.campustix.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.domain.posts.Post
import ua.chnu.campustix.presentation.home.paging.DataErrorException
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.presentation.ui.messageRes
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.home_back
import ua.chnu.campustix.resources.home_empty
import ua.chnu.campustix.resources.home_feed_title
import ua.chnu.campustix.resources.home_greeting
import ua.chnu.campustix.resources.home_load_error
import ua.chnu.campustix.resources.home_logout_short
import ua.chnu.campustix.resources.home_refresh
import ua.chnu.campustix.resources.home_retry
import ua.chnu.campustix.resources.home_select_post
import ua.chnu.campustix.resources.ic_arrow_back
import ua.chnu.campustix.resources.ic_logout
import ua.chnu.campustix.resources.ic_onboarding_reviews
import ua.chnu.campustix.resources.ic_refresh
import ua.chnu.campustix.resources.ic_settings
import ua.chnu.campustix.resources.post_author
import ua.chnu.campustix.resources.post_card_stats
import ua.chnu.campustix.resources.post_dislikes
import ua.chnu.campustix.resources.post_likes
import ua.chnu.campustix.resources.post_views
import ua.chnu.campustix.resources.settings_title

private val DetailMaxWidth = 720.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    onNavigateToAuth: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // One LazyPagingItems for both panes: the detail pane takes the post from the loaded pages
    val posts = viewModel.posts.collectAsLazyPagingItems()
    // The navigator remembers which pane is shown and which post is open (its id is the content key)
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // System back (Android gesture, Esc on desktop/web) closes the post before leaving Home.
    // Registered inside NavDisplay, so it is asked first.
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = navigator.canNavigateBack(),
        onBackCompleted = { scope.launch { navigator.navigateBack() } },
    )

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(getString(event.error.messageRes()))
            }
            HomeEvent.NavigateToSettings -> onOpenSettings()
            HomeEvent.NavigateToAuth -> onNavigateToAuth()
        }
    }

    // A failed refresh over an already shown list: the old posts stay, the error goes to a Snackbar
    // (an item at the top of the list would be unseen if the list is scrolled down)
    val refresh = posts.loadState.refresh
    LaunchedEffect(refresh) {
        if (refresh is LoadState.Error && posts.itemCount > 0) {
            val message = (refresh.error as? DataErrorException)?.error?.messageRes() ?: Res.string.home_load_error
            val result = snackbarHostState.showSnackbar(
                message = getString(message),
                actionLabel = getString(Res.string.home_retry),
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) posts.retry()
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                state = state,
                onRefresh = { posts.refresh() },
                onAction = viewModel::onAction,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        // Narrow window: one pane at a time, list -> detail -> back to list.
        // Wide window (Expanded): list and detail side by side.
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            modifier = Modifier.padding(padding),
            listPane = {
                AnimatedPane {
                    PostsList(
                        posts = posts,
                        selectedPostId = navigator.currentDestination?.contentKey,
                        onPostClick = { postId ->
                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, postId) }
                        },
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    val postId = navigator.currentDestination?.contentKey
                    val post = postId?.let { id -> posts.itemSnapshotList.items.firstOrNull { it.id == id } }
                    // The back button is needed only when the list is hidden (one pane)
                    val isListHidden = navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Hidden
                    PostDetails(
                        post = post,
                        showBack = isListHidden,
                        onBack = { scope.launch { navigator.navigateBack() } },
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    state: HomeState,
    onRefresh: () -> Unit,
    onAction: (HomeAction) -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(Res.string.home_feed_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                state.user?.let { user ->
                    Text(
                        text = stringResource(Res.string.home_greeting, user.fullName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        actions = {
            // refresh() starts the feed again from the first page
            TopBarButton(Res.drawable.ic_refresh, stringResource(Res.string.home_refresh), onRefresh)
            TopBarButton(Res.drawable.ic_settings, stringResource(Res.string.settings_title)) {
                onAction(HomeAction.OnSettingsClick)
            }
            TopBarButton(Res.drawable.ic_logout, stringResource(Res.string.home_logout_short)) {
                onAction(HomeAction.OnLogoutClick)
            }
        },
    )
}

@Composable
private fun TopBarButton(icon: DrawableResource, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painter = painterResource(icon), contentDescription = description)
    }
}

@Composable
private fun PostsList(
    posts: LazyPagingItems<Post>,
    selectedPostId: Int?,
    onPostClick: (Int) -> Unit,
) {
    val refresh = posts.loadState.refresh
    val isEmpty = posts.itemCount == 0

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // the very first page: nothing to show yet
            refresh is LoadState.Loading && isEmpty ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            refresh is LoadState.Error && isEmpty ->
                LoadError(refresh.error, onRetry = posts::retry, modifier = Modifier.align(Alignment.Center))

            refresh is LoadState.NotLoading && posts.loadState.append.endOfPaginationReached && isEmpty ->
                Text(
                    text = stringResource(Res.string.home_empty),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Stable keys: Compose knows which card is which when new pages arrive
                items(count = posts.itemCount, key = posts.itemKey { it.id }) { index ->
                    // posts[index] also tells Paging how far the user has scrolled
                    posts[index]?.let { post ->
                        PostCard(
                            post = post,
                            isSelected = post.id == selectedPostId,
                            onClick = { onPostClick(post.id) },
                        )
                    }
                }

                // the next page at the bottom of the list
                when (val append = posts.loadState.append) {
                    is LoadState.Loading -> item(key = "append_loading") {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is LoadState.Error -> item(key = "append_error") {
                        LoadError(append.error, onRetry = posts::retry)
                    }
                    is LoadState.NotLoading -> Unit
                }
            }
        }
        // refresh() over an already shown list: the bar stays visible wherever the list is scrolled
        if (refresh is LoadState.Loading && !isEmpty) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun LoadError(error: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = (error as? DataErrorException)?.error?.messageRes() ?: Res.string.home_load_error
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(message),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        // retry() repeats only the load that failed
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.home_retry))
        }
    }
}

@Composable
private fun PostCard(post: Post, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors()
        },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = post.tags.joinToString(" ") { "#$it" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.post_card_stats, post.likes, post.views),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PostDetails(post: Post?, showBack: Boolean, onBack: () -> Unit) {
    if (post == null) {
        // Two panes and nothing chosen yet
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_onboarding_reviews),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
            )
            Text(
                text = stringResource(Res.string.home_select_post),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.widthIn(max = DetailMaxWidth),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (showBack) {
                TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Icon(painterResource(Res.drawable.ic_arrow_back), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.home_back))
                }
            }
            Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(Res.string.post_author, post.authorId),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = post.tags.joinToString(" ") { "#$it" },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = post.body, style = MaterialTheme.typography.bodyLarge)
            HorizontalDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Stat(post.likes, stringResource(Res.string.post_likes))
                Stat(post.dislikes, stringResource(Res.string.post_dislikes))
                Stat(post.views, stringResource(Res.string.post_views))
            }
        }
    }
}

@Composable
private fun Stat(value: Int, label: String) {
    Column {
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
