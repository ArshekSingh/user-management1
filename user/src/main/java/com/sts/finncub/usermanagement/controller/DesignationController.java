package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.usermanagement.request.EmployeeDesignationRequest;
import com.sts.finncub.usermanagement.service.DesignationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/designation")
public class DesignationController {


    private final DesignationService designationService;


    @Autowired
    public DesignationController(DesignationService designationService) {
        this.designationService = designationService;
    }

    @GetMapping("/fetchAllEmployeeDesignation")
    public Response getAllEmployeeDesignationDetails() throws BadRequestException {
        return designationService.getAllEmployeeDesignationDetails();
    }

    @GetMapping("/getEmployeeDesignation/{designationId}")
    public Response getEmployeeDesignationDetail(@PathVariable Long designationId)
            throws BadRequestException {
        return designationService.getEmployeeDesignationDetail(designationId);
    }

    @PostMapping("/add")
    public Response addEmployeeDesignation(@RequestBody EmployeeDesignationRequest request)
            throws BadRequestException {
        return designationService.addEmployeeDesignation(request);
    }

    @PostMapping("/update")
    public Response updateEmployeeDesignation(@RequestBody EmployeeDesignationRequest request)
            throws BadRequestException {
        return designationService.updateEmployeeDesignation(request);
    }
}
