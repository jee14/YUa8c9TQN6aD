package com.yua8c9tqn6ad.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatConfig(
    private val chatClient: ChatClient.Builder,
) {
    @Bean
    fun chatClient(): ChatClient {
        return chatClient.defaultAdvisors(
            MessageChatMemoryAdvisor(
                InMemoryChatMemory(),
            ),
        ).build()
    }
}
