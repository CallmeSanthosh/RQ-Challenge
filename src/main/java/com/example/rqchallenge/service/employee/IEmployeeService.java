package com.example.rqchallenge.service.employee;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.rqchallenge.modal.Employee;
import com.fasterxml.jackson.databind.JsonNode;

public interface IEmployeeService {
	public List<Employee> getAllEmployees();
	public List<Employee> getEmployeeByNameSearch(String searchString);
	public Employee getEmployeeById(String id);
	public Integer getHighestSalaryOfEmployees();
	public List<String> getTopTenHighestEarningEmployeeNames();
	public Employee createEmployee(Map<String, Object> employeeInput);
	public String deleteEmployeeById(String id);
}
