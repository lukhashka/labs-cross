package com.example.block_3.presentation

// One-time side effects (shown once, not stored in the state).
sealed class MainEvent {
    data class ShowError(val message: String) : MainEvent()
}
