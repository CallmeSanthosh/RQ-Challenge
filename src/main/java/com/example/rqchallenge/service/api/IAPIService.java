package com.example.rqchallenge.service.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface IAPIService {
	public ResponseEntity<JsonNode> getRequest(String endpoint);
	public ResponseEntity<JsonNode> postRequest(String endpoint, Map<String, Object> requestData);
	public ResponseEntity<JsonNode> deleteRequest(String endpoint);
}
