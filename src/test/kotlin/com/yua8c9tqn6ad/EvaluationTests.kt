package com.yua8c9tqn6ad

import com.yua8c9tqn6ad.domain.Chat
import com.yua8c9tqn6ad.service.ChatService
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.ai.model.Content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.chromadb.ChromaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertTrue

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
class EvaluationTests {
    companion object {
        @Container
        @JvmField
        val chroma: ChromaDBContainer = ChromaDBContainer("ghcr.io/chroma-core/chroma:0.5.4")

        @JvmStatic
        @DynamicPropertySource
        fun chromaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.ai.vectorstore.chroma.client.host") { "http://${chroma.host}" }
            registry.add("spring.ai.vectorstore.chroma.client.port") { chroma.getMappedPort(8000) }
        }
    }

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var chatModel: ChatModel

    @Test
    fun `test evaluation`() {
        val input = "How do I sign up for the Smart Store Center membership?"
        val response = chatService.chatForResponse(Chat(input))
        val responseContent = response.result.output.content
        val relevancyEvaluator = RelevancyEvaluator(ChatClient.builder(chatModel))
        val responseDocuments =
            response.metadata.get<Any>(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS) as? List<Content>
        val evaluationRequest = EvaluationRequest(
            input,
            responseDocuments,
            responseContent,
        )
        val evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest)

        assertTrue(
            evaluationResponse.isPass,
            "Response is not relevant to the asked question.\n" +
                "Question: " + input + "\n" +
                "Response: " + responseContent,
        )
    }
}
