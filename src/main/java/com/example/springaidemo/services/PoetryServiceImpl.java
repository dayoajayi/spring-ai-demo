package com.example.springaidemo.services;

import com.example.springaidemo.dto.PoetryDto;
import com.example.springaidemo.simple.Completion;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.parser.BeanOutputParser;
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
    public Completion getCatHaiku() {
        return new Completion(chatClient.call(WRITE_ME_HAIKU_ABOUT_CAT));
    }

   /*@Override
    public PoetryDto getPoetryByGenreAndTheme(String genre, String theme) {
        BeanOutputParser<PoetryDto> poetryDtoBeanOutputParser = new BeanOutputParser<>(PoetryDto.class);

        String promptString = """
                Write me {genre} poetry about {theme}
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptString);
        promptTemplate.add("genre", genre);
        promptTemplate.add("theme", theme);
        promptTemplate.add("format", poetryDtoBeanOutputParser.getFormat());

        promptTemplate.setOutputParser(poetryDtoBeanOutputParser);

        ChatResponse response = chatClient.generate(promptTemplate.create());
        return poetryDtoBeanOutputParser.parse(response.getGeneration().getText());
    }*/
}