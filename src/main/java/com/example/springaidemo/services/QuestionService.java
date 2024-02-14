package com.example.springaidemo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private final SearchRequest searchRequest;

    public QuestionService(ChatClient chatClient, VectorStore vectorStore, SearchRequest searchRequest) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
    }

    public String generate(String message, boolean stuffit){
        Message systemMessage = getSystemMessage(message, stuffit);
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        logger.info("Asking AI model to reply to question.");
        ChatResponse aiResponse = chatClient.call(prompt);
        logger.info("AI responded.");
        return aiResponse.getResult().toString();
    }

    private Message getSystemMessage(String message, boolean stuffit) {
        if (stuffit) {
            logger.info("Retrieving relevant documents");
            searchRequest.withQuery(message);
            List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
            logger.info(String.format("Found %s relevant documents.", similarDocuments.size()));
            String documents = similarDocuments.stream().map(entry -> entry.getContent()).collect(Collectors.joining("\n"));
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("answering from document");
            Message  messageResult = systemPromptTemplate.createMessage(Map.of("documents", documents));
            return messageResult;
        } else {
            logger.info("Not stuffing the prompt, using generic prompt");
            return new SystemPromptTemplate("answering without document").createMessage();
        }
    }
}
