package com.yua8c9tqn6ad.controller

import com.yua8c9tqn6ad.domain.Chat
import com.yua8c9tqn6ad.service.ChatService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @MessageMapping("/messages/{chatId}")
    fun chat(
        @DestinationVariable chatId: Long,
        input: String,
    ) {
        val output = chatService.chat(
            Chat(input)
        )
        simpMessagingTemplate.convertAndSend("/subscribes/$chatId", output.content)
    }
}
