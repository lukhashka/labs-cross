package com.example.block_3.presentation

// Intents: everything the user can do on the screen.
sealed class MainAction {
    data class OnTitleChange(val title: String) : MainAction()
    data class OnBodyChange(val body: String) : MainAction()
    data object OnGetClick : MainAction()
    data object OnPostClick : MainAction()
    data object OnPutClick : MainAction()
    data object OnDeleteClick : MainAction()
}
