package com.example.springaidemo.services;

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PoetryServiceImpl implements PoetryService {

    public static final String WRITE_ME_HAIKU_ABOUT_CAT = """
           Write me Haiku about cat,
           haiku should start with the word cat obligatory""";
    private final ChatClient chatClient;

    @Autowired
    public PoetryServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @Override
    public String getCatHaiku() {
        chatClient.call(WRITE_ME_HAIKU_ABOUT_CAT);
        return chatClient.call(WRITE_ME_HAIKU_ABOUT_CAT);
    }

}