package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.request.FilterRequest;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.EmployeeRequest;
import com.sts.finncub.usermanagement.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return employeeService.getAllEmployeeDetails(request);
    }

    @GetMapping("/getEmployeeDetail/{employeeId}")
    public Response getEmployeeDetail(@PathVariable Long employeeId) throws BadRequestException {
        return employeeService.getEmployeeDetail(employeeId);
    }

    @PostMapping("/add")
    public Response addEmployee(@RequestBody EmployeeRequest request) throws BadRequestException {
        return employeeService.addEmployee(request);
    }

    @PostMapping("/update")
    public Response updateEmployeeDetails(@RequestBody EmployeeRequest request) throws BadRequestException {
        return employeeService.updateEmployeeDetails(request);
    }

    @PostMapping("/employeePackageTransfer")
    public Response employeeTransferPackageCall(@RequestBody FilterRequest request) throws BadRequestException {
        return employeeService.employeeTransferPackageCall(request);
    }

    @GetMapping(value = "getSubEmpDeptByEmpDepartmentId/{empDepartmentId}")
    public Response getSubEmpDeptByEmpDepartmentId(@PathVariable Long empDepartmentId) {
        return employeeService.getSubEmpDeptByEmpDepartmentId(empDepartmentId);
    }
}