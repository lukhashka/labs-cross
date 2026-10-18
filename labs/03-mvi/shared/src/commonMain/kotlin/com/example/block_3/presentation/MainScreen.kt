package com.example.block_3.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.block_3.presentation.ui.ObserveEvents
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is MainEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar("Помилка: ${event.message}")
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        MainContent(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(padding),
        )
    }
}

// Stateless: draws the state and sends actions, no logic inside.
@Composable
private fun MainContent(
    state: MainState,
    onAction: (MainAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAction(MainAction.OnGetClick) }, enabled = !state.isLoading) { Text("GET") }
            Button(onClick = { onAction(MainAction.OnPostClick) }, enabled = !state.isLoading) { Text("POST") }
            Button(onClick = { onAction(MainAction.OnPutClick) }, enabled = !state.isLoading) { Text("PUT") }
            Button(onClick = { onAction(MainAction.OnDeleteClick) }, enabled = !state.isLoading) { Text("DELETE") }
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        OutlinedTextField(
            value = state.titleInput,
            onValueChange = { onAction(MainAction.OnTitleChange(it)) },
            label = { Text("Заголовок") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.bodyInput,
            onValueChange = { onAction(MainAction.OnBodyChange(it)) },
            label = { Text("Текст") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Text(
            text = state.lastOperation?.let { "Остання операція: $it" } ?: "Очікування дії...",
            style = MaterialTheme.typography.titleMedium,
        )

        state.post?.let { post ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Пост #${post.id} (автор ${post.authorId})", style = MaterialTheme.typography.labelLarge)
                    Text(post.title, style = MaterialTheme.typography.titleMedium)
                    Text(post.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
