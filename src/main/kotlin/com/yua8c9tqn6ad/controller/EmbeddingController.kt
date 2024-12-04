package com.yua8c9tqn6ad.controller

import org.slf4j.LoggerFactory
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
class EmbeddingController(
    private val vectorStore: VectorStore,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @Value("classpath:/documents/naver_smartstore.json")
    private lateinit var resource: Resource

    @PutMapping("/embeddings")
    fun embed() {
        val stopWatch = StopWatch()
        log.info("start to splitting")
        stopWatch.start()

        val textReader = TextReader(resource)
        val splitDocuments = TokenTextSplitter().split(textReader.read())
        splitDocuments.forEach { document ->
            vectorStore.add(mutableListOf(document))
        }

        stopWatch.stop()
        log.info("end: ${stopWatch.getTotalTime(TimeUnit.SECONDS)}")
    }
}
