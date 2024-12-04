package com.yua8c9tqn6ad.controller

import com.yua8c9tqn6ad.domain.Chat
import com.yua8c9tqn6ad.service.ChatService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatController(
    private val chatService: ChatService,
) {
    @PostMapping("/chats")
    fun chats(
        @RequestBody input: String,
    ): Flux<String> {
        return chatService.chat(
            Chat(input),
        )
    }
}
