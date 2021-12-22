package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeSubDepartmentRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.SubDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/subDepartment")
public class SubDepartmentController {

    private final SubDepartmentService subDepartmentService;

    @Autowired
    public SubDepartmentController(SubDepartmentService subDepartmentService) {
        this.subDepartmentService = subDepartmentService;
    }

    @GetMapping("/fetchAllEmployeeDepartment")
    public Response getAllSubDepartmentDetails() throws BadRequestException {
        return subDepartmentService.getAllSubDepartmentDetails();
    }

    @GetMapping("/getEmployeeSubDepartment/{subDepartmentId}")
    public Response getSubDepartmentDetails(@PathVariable Long subDepartmentId) throws BadRequestException {
        return subDepartmentService.getSubDepartmentDetails(subDepartmentId);
    }

    @PostMapping("/add")
    public Response addSubDepartmentDetails(@RequestBody EmployeeSubDepartmentRequest request) throws BadRequestException {
        return subDepartmentService.addSubDepartmentDetails(request);
    }

    @PostMapping("/update")
    public Response updateSubDepartmentDetails(@RequestBody EmployeeSubDepartmentRequest request) throws BadRequestException {
        return subDepartmentService.updateSubDepartmentDetails(request);
    }
}
