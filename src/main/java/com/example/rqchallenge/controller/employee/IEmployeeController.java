package com.example.rqchallenge.controller.employee;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.rqchallenge.modal.Employee;

public interface IEmployeeController {
	public ResponseEntity<List<Employee>> getAllEmployees();
	public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString);
	public ResponseEntity<Employee> getEmployeeById(@PathVariable String id);
	public ResponseEntity<Integer> getHighestSalaryOfEmployees();
	public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();
	public ResponseEntity<Employee> createEmployee(@RequestBody Map<String, Object> employeeInput);
	public ResponseEntity<String> deleteEmployeeById(@PathVariable String id);
}
