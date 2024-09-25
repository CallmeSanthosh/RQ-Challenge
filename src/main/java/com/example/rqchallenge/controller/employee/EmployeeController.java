package com.example.rqchallenge.controller.employee;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rqchallenge.modal.Employee;
import com.example.rqchallenge.service.employee.IEmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController implements IEmployeeController {

	@Autowired
	private IEmployeeService employeeService;

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@GetMapping()
	public ResponseEntity<List<Employee>> getAllEmployees() {

		List<Employee> allEmployees = employeeService.getAllEmployees();
		return ResponseEntity.ok(allEmployees);
	}

	@GetMapping("/search/{searchString}")
	public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
		List<Employee> matchingNames = employeeService.getEmployeeByNameSearch(searchString);
		System.out.println("allEmployees : " + matchingNames);
		return ResponseEntity.ok(matchingNames);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
		Employee employee = employeeService.getEmployeeById(id);
		return ResponseEntity.ok(employee);
	}

	@GetMapping("/highestSalary")
	public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
		Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
		return ResponseEntity.ok(highestSalary);
	}

	@GetMapping("/topTenHighestEarningEmployeeNames")
	public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
		List<String> topTenEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
		return ResponseEntity.ok(topTenEmployeeNames);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Employee> createEmployee(@RequestBody Map<String, Object> employeeInput) {
		Employee Employee = employeeService.createEmployee(employeeInput);
		return ResponseEntity.ok(Employee);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
		String employeeName = employeeService.deleteEmployeeById(id);
		return ResponseEntity.ok(employeeName);
	}

}
