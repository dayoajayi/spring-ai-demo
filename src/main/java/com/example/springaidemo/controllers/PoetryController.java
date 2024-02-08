package com.example.springaidemo.controllers;

import com.example.springaidemo.services.PoetryService;
import com.example.springaidemo.simple.Completion;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ai")
public class PoetryController {
    private final PoetryService poetryService;
    private final JdbcTemplate jdbcTemplate;

    public PoetryController(PoetryService poetryService) {
        this.poetryService = poetryService;
    }

    // constructor

    @GetMapping("/cathaiku")
    public ResponseEntity<Completion> generateHaiku(){
        return ResponseEntity.ok(poetryService.getCatHaiku());
    }

    @GetMapping("/count")
    public int count() {
        String sql = "SELECT COUNT(*) FROM vector_store";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}