package com.example.springaidemo.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Config {

    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient, JdbcTemplate jdbcTemplate) {
        return new PgVectorStore(jdbcTemplate, embeddingClient);
    }

    @Bean
    public SearchRequest searchRequest() {
        SearchRequest searchRequest = SearchRequest.defaults();
        searchRequest.withTopK(4);
        searchRequest.withSimilarityThreshold(0.75);
        return searchRequest;
    }
}
