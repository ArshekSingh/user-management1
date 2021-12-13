package com.sts.finncub.usermanagement.controller;


import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.EmployeeDepartmentRoleRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.EmployeeDepartmentRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/roleMaster")
public class EmployeeDepartmentRoleController {

    private final EmployeeDepartmentRoleService employeeDepartmentRoleService;

    @Autowired
    public EmployeeDepartmentRoleController(EmployeeDepartmentRoleService employeeDepartmentRoleService) {
        this.employeeDepartmentRoleService = employeeDepartmentRoleService;
    }

    @GetMapping("/fetchAllEmployeeDepartmentRoles")
    public Response getAllEmployeeDepartmentRoles() throws BadRequestException {
        return employeeDepartmentRoleService.getAllEmployeeDepartmentRoles();
    }

    @GetMapping("/getEmployeeDepartmentRole/{empDeptRoleId}")
    public Response getEmployeeDepartmentRole(@PathVariable Long empDeptRoleId) throws BadRequestException {
        return employeeDepartmentRoleService.getEmployeeDepartmentRole(empDeptRoleId);
    }

    @PostMapping("/add")
    public Response addEmployeeDepartmentRole(@RequestBody EmployeeDepartmentRoleRequest request)
            throws BadRequestException {
        return employeeDepartmentRoleService.addEmployeeDepartmentRole(request);
    }

    @PostMapping("/update")
    public Response updateEmployeeDepartmentRole(@RequestBody EmployeeDepartmentRoleRequest request)
            throws BadRequestException {
        return employeeDepartmentRoleService.updateEmployeeDepartmentRole(request);
    }
}
