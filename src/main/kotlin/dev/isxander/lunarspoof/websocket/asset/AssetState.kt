package dev.isxander.lunarspoof.websocket.asset

enum class AssetState {
    DISCONNECTED,
    AWAITING_ENCRYPTION_REQUEST,
    AUTHENTICATING,
    READY,
}