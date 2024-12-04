package com.yua8c9tqn6ad.config

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmbeddingConfig {
    @Bean
    fun embeddingModel(): EmbeddingModel {
        return OpenAiEmbeddingModel(OpenAiApi(System.getenv("OPENAI_API_KEY")))
    }
}
