package com.aethermind.app

data class ChatMessage(
    val text: String,
    val sender: Sender
) {
    enum class Sender { USER, AETHER }
}
