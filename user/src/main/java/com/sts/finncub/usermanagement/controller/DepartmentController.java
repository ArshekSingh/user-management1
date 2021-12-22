package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.EmployeeDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/department")
public class DepartmentController {

    private final EmployeeDepartmentService employeeDepartmentService;

    @Autowired
    public DepartmentController(EmployeeDepartmentService employeeDepartmentService) {
        this.employeeDepartmentService = employeeDepartmentService;
    }

    @GetMapping("/fetchAllEmployeeDepartment")
    public Response getAllEmployeeDepartmentDetails() throws BadRequestException {
        return employeeDepartmentService.getAllEmployeeDepartmentDetails();
    }

    @GetMapping("/getEmployeeDepartment/{departmentId}")
    public Response getEmployeeDepartmentDetail(@PathVariable Long departmentId) throws BadRequestException {
        return employeeDepartmentService.getEmployeeDepartmentDetail(departmentId);
    }

    @PostMapping("/add")
    public Response addEmployeeDepartment(@RequestBody EmployeeDepartmentRequest request) throws BadRequestException {
        return employeeDepartmentService.addEmployeeDepartment(request);
    }

    @PostMapping("/update")
    public Response updateEmployeeDepartment(@RequestBody EmployeeDepartmentRequest request) throws BadRequestException {
        return employeeDepartmentService.updateEmployeeDepartment(request);
    }
}
