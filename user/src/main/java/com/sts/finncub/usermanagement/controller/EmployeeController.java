package com.sts.finncub.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/employee")
public class EmployeeController {

	private final EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@PostMapping("/fetchAllEmployee")
	public Response getAllEmployeeDetails(@RequestBody FilterRequest request) throws BadRequestException {
		log.info("getAllEmployeeDetails invoked ");
		return employeeService.getAllEmployeeDetails(request);
	}

	@GetMapping("/getEmployeeDetail/{employeeId}")
	public Response getEmployeeDetail(@PathVariable Long employeeId) throws BadRequestException {
		log.info("getEmployeeDetail invoked for employeeId : {}", employeeId);
		return employeeService.getEmployeeDetail(employeeId);
	}

	@PostMapping("/add")
	public Response addEmployee(@RequestBody EmployeeRequest request) throws BadRequestException {
		log.info("addEmployee invoked for {}", request.getFirstName());
		return employeeService.addEmployee(request);
	}

	@PostMapping("/update")
	public Response updateEmployeeDetails(@RequestBody EmployeeRequest request) throws BadRequestException {
		log.info("updateEmployeeDetails invoked for employeeId : {}", request.getEmployeeId());
		return employeeService.updateEmployeeDetails(request);
	}

	@PostMapping("/employeePackageTransfer")
	public Response employeeTransferPackageCall(@RequestBody FilterRequest request) throws BadRequestException {
		log.info("employeeTransferPackageCall invoked for employeeId : {}", request.getEmployeeId());
		return employeeService.employeeTransferPackageCall(request);
	}

	@GetMapping(value = "getSubEmpDeptByEmpDepartmentId/{empDepartmentId}")
	public Response getSubEmpDeptByEmpDepartmentId(@PathVariable Long empDepartmentId) {
		return employeeService.getSubEmpDeptByEmpDepartmentId(empDepartmentId);
	}
}