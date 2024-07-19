package com.example.codeopus.api

data class GPTChatRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int,
    val temperature: Double
)

data class Message(
    val role: String,
    val content: String
)

data class GPTChatResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

