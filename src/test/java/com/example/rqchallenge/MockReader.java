package com.example.rqchallenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class MockReader {
    public static JsonNode getMockData(String filename) {
        URL resource = MockReader.class.getClassLoader().getResource(filename);
        try {
            return new ObjectMapper().readTree(resource);
        } catch (IOException ex) {
            return null;
        }
    }
}