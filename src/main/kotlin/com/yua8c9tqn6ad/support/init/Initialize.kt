package com.yua8c9tqn6ad.support.init

import org.slf4j.LoggerFactory
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import java.util.concurrent.TimeUnit

@Component
class Initialize(
    private val vectorStore: VectorStore,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @Value("classpath:/documents/final_result.json")
    private lateinit var mdResource: Resource

    override fun run(args: ApplicationArguments?) {
        val stopWatch = StopWatch()
        log.info("start to splitting")
        stopWatch.start()

        val textReader = TextReader(mdResource)
        val splitDocuments = TokenTextSplitter().split(textReader.read())
        splitDocuments.forEach { document ->
            vectorStore.add(mutableListOf(document))
        }

        stopWatch.stop()
        log.info("end: ${stopWatch.getTotalTime(TimeUnit.SECONDS)}")
    }
}
