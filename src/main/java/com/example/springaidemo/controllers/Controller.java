package com.example.springaidemo.controllers;

import com.example.springaidemo.services.DataLoadingService;
import com.example.springaidemo.services.PoetryService;
import com.example.springaidemo.services.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("ai")
public class Controller {
    private final PoetryService poetryService;
    private final JdbcTemplate jdbcTemplate;

    private final QuestionService questionService;

    private final DataLoadingService dataLoadingService;

    public Controller(PoetryService poetryService, JdbcTemplate jdbcTemplate,
                      DataLoadingService dataLoadingService,
                      QuestionService questionService) {
        this.poetryService = poetryService;
        this.jdbcTemplate = jdbcTemplate;
        this.dataLoadingService = dataLoadingService;
        this.questionService = questionService;
    }

    @GetMapping("/cathaiku")
    public ResponseEntity<String> generateHaiku() {
        return ResponseEntity.ok(poetryService.getCatHaiku());
    }

    @GetMapping("/count")
    public int count() {
        String sql = "SELECT COUNT(*) FROM vector_store";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @PostMapping("/load")
    public ResponseEntity<String> load() {
        try {
            dataLoadingService.load();
            return ResponseEntity.ok("file uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while loading data: " + e.getMessage());
        }
    }

    @PostMapping("/load/document")
    public ResponseEntity<String> loadDocument(@RequestBody String document) {
        try {
            dataLoadingService.loadDocument(document);
            return ResponseEntity.ok("document uploaded successfuly");
        } catch (
                Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while loading data: " + e.getMessage());
        }
    }

    @GetMapping("/load/json")
    public ResponseEntity<String> loadJson(){
        dataLoadingService.loadJson();
        return ResponseEntity.ok("done");
    }

    @GetMapping("/qa")
    public Map answerQuestion(@RequestParam(value = "question", defaultValue =  "is Earth flat?") String question,
                              @RequestParam(value = "stuffit", defaultValue = "true") boolean stuffit) {
        String answer = questionService.generate(question, stuffit);
        Map map = new LinkedHashMap();
        map.put("question", question);
        map.put("answer", answer);
        return map;
    }
}