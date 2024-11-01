package com.yua8c9tqn6ad.service

import com.yua8c9tqn6ad.domain.Chat
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class ChatService(
    private val vectorStore: VectorStore,
    private val chatClient: ChatClient,
) {
    @Value("classpath:prompts/chat/system-message.st")
    private lateinit var systemResource: Resource

    fun chat(input: Chat): Chat {
        val relatedDocuments = vectorStore.similaritySearch(input.content)
        val documents = relatedDocuments.stream()
            .map(Document::getContent)
            .collect(Collectors.joining(System.lineSeparator()))
        val response = chatClient
            .prompt()
            .system { s ->
                s.text(systemResource).params(mapOf(Pair("documents", documents)))
            }
            .user(input.content)
            .call()
            .content()
        return Chat(response)
    }

    fun chatForResponse(input: Chat): ChatResponse {
        val response = chatClient
            .prompt()
            .advisors(
                QuestionAnswerAdvisor(vectorStore),
            )
            .user(input.content)
            .call()
            .chatResponse()
        return response
    }
}
