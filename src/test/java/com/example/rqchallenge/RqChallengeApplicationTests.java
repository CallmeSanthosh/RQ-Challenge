package com.example.rqchallenge;

import com.example.rqchallenge.controller.employee.EmployeeController;
import com.example.rqchallenge.modal.Employee;
import com.example.rqchallenge.service.api.APIService;
import com.example.rqchallenge.service.employee.EmployeeService;
import com.example.rqchallenge.utils.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
class RqChallengeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	RestTemplate restTemplate;

	@InjectMocks
	private EmployeeController controller;

	@Value("${api.base.url}")
	private String apiBaseUrl;

	private static JsonNode employees;
	private static JsonNode singleEmployee;
	private static List<String> highestEarners;

	private void mockEmployeesResponse() {
		when(restTemplate.getForEntity(apiBaseUrl + Constants.EMPLOYEES, JsonNode.class))
				.thenReturn(ResponseEntity.ok(employees));
	}

	private void mockSingleEmployeeResponse() {
		when(restTemplate.getForEntity(apiBaseUrl + Constants.EMPLOYEES + Constants.SEPARATOR + "1", JsonNode.class))
				.thenReturn(ResponseEntity.ok(singleEmployee));
	}

	@BeforeAll
	public static void init() {
		employees = MockReader.getMockData("mockdata/get-employees-data.json");
		assertNotNull(employees);
		singleEmployee = MockReader.getMockData("mockdata/single-employee-data.json");
		assertNotNull(singleEmployee);
	}

	@Test
	void getAllEmployees() throws Exception {
		mockEmployeesResponse();
		mockMvc.perform(get("/employees")).andExpect(status().isOk()).andDo(result -> {
			List<Employee> employeesList = objectMapper.readValue(result.getResponse().getContentAsString(),
					new TypeReference<>() {
					});
			assertEquals(50, employeesList.size());
		});
	}

	@Test
	void getEmployeesByNameSearch() throws Exception {
		mockEmployeesResponse();
		mockMvc.perform(get("/employees/search/John")).andExpect(status().isOk()).andDo(result -> {
			List<Employee> employeesList = objectMapper.readValue(result.getResponse().getContentAsString(),
					new TypeReference<>() {
					});
			assertEquals(4, employeesList.size());
		});
	}

	@Test
	void getEmployeeById() throws Exception {
		mockSingleEmployeeResponse();
		mockMvc.perform(get("/employees/1")).andExpect(status().isOk()).andDo(result -> {
			Employee employee = objectMapper.readValue(result.getResponse().getContentAsString(), Employee.class);
			assertEquals(employee.getEmployeeName(), "John Doe");
			assertEquals(employee.getEmployeeSalary(), 90096);
			assertEquals(employee.getEmployeeAge(), 25);
		});
	}

	@Test
	void getHighestSalaryOfEmployees() throws Exception {
		mockEmployeesResponse();
		mockMvc.perform(get("/employees/highestSalary")).andExpect(status().isOk())
				.andDo(result -> assertEquals("118054", result.getResponse().getContentAsString()));
	}

	@Test
	void getTopTenHighestEarningEmployeeNames() throws Exception {
		mockEmployeesResponse();
		highestEarners = Arrays.asList("Avery Sanchez", "Emily Davis", "Michael Johnson", "John Brown", "Grace Scott",
				"Victoria Nelson", "John Doe", "Madison Cook", "Matthew Perez", "William Gonzalez");
		mockMvc.perform(get("/employees/topTenHighestEarningEmployeeNames")).andExpect(status().isOk())
				.andDo(result -> {
					List<String> employeeNames = objectMapper.readValue(result.getResponse().getContentAsString(),
							new TypeReference<>() {
							});
					assertEquals(10, employeeNames.size());
					System.out.println(employeeNames);
					assertIterableEquals(highestEarners, employeeNames);
				});
	}

	@Test
	void createEmployee() throws Exception {
		Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
		inputMap.put("name", "John Doe");
		inputMap.put("salary", 90096);
		inputMap.put("age", 25);
		String requestBody = objectMapper.writeValueAsString(inputMap);

		when(restTemplate.postForEntity(apiBaseUrl + Constants.CREATE, inputMap, JsonNode.class))
				.thenReturn(ResponseEntity.ok(singleEmployee));

		mockMvc.perform(post("/employees").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(result -> {
					Employee employee = objectMapper.readValue(result.getResponse().getContentAsString(),
							Employee.class);
					assertEquals(employee.getEmployeeName(), "John Doe");
					assertEquals(employee.getEmployeeSalary(), 90096);
					assertEquals(employee.getEmployeeAge(), 25);
				});
	}

	@Test
	void deleteEmployeeById() throws Exception {
		mockSingleEmployeeResponse();
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		responseMap.put("status", "success");
		responseMap.put("message", "successfully! deleted Record");
		JsonNode responseBody = objectMapper.valueToTree(responseMap);

		when(restTemplate.exchange(apiBaseUrl + Constants.DELETE + "/1", HttpMethod.DELETE, null, JsonNode.class))
				.thenReturn(ResponseEntity.ok(responseBody));

		mockMvc.perform(delete("/employees/1")).andExpect(status().isOk())
				.andDo(result -> assertEquals("John Doe", result.getResponse().getContentAsString()));
	}

}