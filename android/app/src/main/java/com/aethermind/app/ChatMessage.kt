package com.aethermind.app

data class ChatMessage(
    var text: String,
    val sender: Sender
) {
    enum class Sender { USER, AETHER }
}
