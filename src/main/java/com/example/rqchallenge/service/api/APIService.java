package com.example.rqchallenge.service.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class APIService implements IAPIService {

	@Autowired
	private RestTemplate restTemplate;
	
    @Value("${api.base.url}")
    private String apiBaseUrl;
    
    private static final Logger logger = LoggerFactory.getLogger(APIService.class);
	
	public ResponseEntity<JsonNode> getRequest(String path) {
		logger.info("GET Request: {}{}", apiBaseUrl, path);
		return restTemplate.getForEntity(apiBaseUrl + path, JsonNode.class);
	}
	
	public ResponseEntity<JsonNode> postRequest(String path, Map<String, Object> requestData) {
		logger.info("POST Request: {}{}", apiBaseUrl, path);
		return restTemplate.postForEntity(apiBaseUrl + path, requestData, JsonNode.class);
	}
	
	public ResponseEntity<JsonNode> deleteRequest(String path) {
		logger.info("DELETE Request: {}{}", apiBaseUrl, path);
		return restTemplate.exchange(apiBaseUrl + path, HttpMethod.DELETE,null,JsonNode.class);
	}
}
