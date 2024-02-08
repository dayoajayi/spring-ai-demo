package com.example.springaidemo.services;

import com.example.springaidemo.dto.PoetryDto;
import com.example.springaidemo.simple.Completion;

public interface PoetryService {
    Completion getCatHaiku();

   // PoetryDto getPoetryByGenreAndTheme(String genre, String theme);
}
