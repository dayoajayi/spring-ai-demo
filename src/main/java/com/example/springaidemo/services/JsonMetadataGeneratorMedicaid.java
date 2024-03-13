package com.example.springaidemo.services;

import org.springframework.ai.reader.JsonMetadataGenerator;

import java.util.HashMap;
import java.util.Map;

public class JsonMetadataGeneratorMedicaid implements JsonMetadataGenerator {
    @Override
    public Map<String, Object> generate(Map<String, Object> jsonMap) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Page", jsonMap.get("Page"));
        metadata.put("filename", "IL_medicaid");
        metadata.put("format", "json");
        return metadata;

    }
}
