package com.example.rqchallenge.service.employee;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.rqchallenge.exception.ExceptionHandler;
import com.example.rqchallenge.modal.Employee;
import com.example.rqchallenge.modal.Response;
import com.example.rqchallenge.service.api.IAPIService;
import com.example.rqchallenge.utils.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmployeeService implements IEmployeeService{
	
	@Autowired
	private IAPIService apiService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ExceptionHandler exceptionHandler;
	
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class); 
	
	@Override
	public List<Employee> getAllEmployees() {
		logger.error("Service Endpoint for getAllEmployees():", Constants.EMPLOYEES);
		ResponseEntity<JsonNode> response = apiService.getRequest(Constants.EMPLOYEES);
        return processResponse(response, new TypeReference<>() {});
	}
	
	@Override
	public List<Employee> getEmployeeByNameSearch(String searchString){
		logger.error("Service Endpoint for getEmployeeByNameSearch():", Constants.EMPLOYEES+Constants.SEPARATOR+searchString);
		return getAllEmployees().stream()
					.filter(employee -> employee.getEmployeeName() != null && employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
					.collect(Collectors.toList());
		
	}
	
	@Override
	public Employee getEmployeeById(String id) {
		logger.error("Service Endpoint for getEmployeeById():", Constants.EMPLOYEES+Constants.SEPARATOR+id);
		ResponseEntity<JsonNode> response = apiService.getRequest(Constants.EMPLOYEES+Constants.SEPARATOR+id);
		return processResponse(response, new TypeReference<>() {});
	}
	
	@Override
	public Integer getHighestSalaryOfEmployees() {
		logger.error("Service Endpoint for getHighestSalaryOfEmployees()");
		return getAllEmployees().stream()
					.max(Comparator.comparing(Employee::getEmployeeSalary))
					.get().getEmployeeSalary();
	}
	
	@Override
	public List<String> getTopTenHighestEarningEmployeeNames() {
		logger.error("Service Endpoint for getTopTenHighestEarningEmployeeNames()");
		return getAllEmployees().stream()
					.sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
					.limit(10)
					.flatMap(employee -> Stream.of(employee.getEmployeeName()))
					.collect(Collectors.toList());
	}
	
	@Override
	public Employee createEmployee(Map<String, Object> employeeInput) {
		logger.error("Service Endpoint for createEmployee():"+ Constants.CREATE);

		ResponseEntity<JsonNode> response = apiService.postRequest(Constants.CREATE, employeeInput);
		return processResponse(response, new TypeReference<>() {});
	}
	
	@Override
	public String deleteEmployeeById(String id) {
		logger.error("Service Endpoint for deleteEmployeeById():"+ Constants.DELETE+Constants.SEPARATOR+id);
		ResponseEntity<JsonNode> responseEntity = apiService.deleteRequest(Constants.DELETE+Constants.SEPARATOR+id);
        Response response = objectMapper.convertValue(responseEntity.getBody(), Response.class);
		if(response.getStatus().equals(Constants.SUCCESS)) {
			Employee employee = getEmployeeById(id);
			return employee.getEmployeeName();
		}
		return null;
	}
	
	private <T> T processResponse(ResponseEntity<JsonNode> responseEntity, TypeReference<T> type) {
		 if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            logger.info("API successful, Status Code: {}", responseEntity.getStatusCodeValue());
	            Response response = objectMapper.convertValue(responseEntity.getBody(), Response.class);
	            if (Constants.SUCCESS.equalsIgnoreCase(response.getStatus())) {
	            	return objectMapper.convertValue(response.getData(), type);
	            }
	            else {
	                logger.error("API failed, Status: {}", response.getStatus());
	                throw new RuntimeException(Constants.INTERNAL_SERVER_ERROR);
	            }
	        } 
		 else {
	            logger.error("API failed, Status Code: {}", responseEntity.getStatusCodeValue());
	            throw exceptionHandler.exceptionByStatus(responseEntity);
	        }
	    }
}

