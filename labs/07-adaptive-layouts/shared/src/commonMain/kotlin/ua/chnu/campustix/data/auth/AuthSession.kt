package ua.chnu.campustix.data.auth

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

// Signal from the network layer: the refresh token is rejected, the user must sign in again.
class AuthSession {
    private val _expired = Channel<Unit>(capacity = Channel.CONFLATED)
    val expired: Flow<Unit> = _expired.receiveAsFlow()

    fun notifyExpired() {
        _expired.trySend(Unit)
    }
}
