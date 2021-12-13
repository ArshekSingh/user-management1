package com.sts.finncub.usermanagement.controller;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.FunctionalTitleRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.FunctionalTitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/functionTitle")
public class FunctionalTitleController {

    private final FunctionalTitleService functionalTitleService;

    @Autowired
    public FunctionalTitleController(FunctionalTitleService functionalTitleService) {
        this.functionalTitleService = functionalTitleService;
    }

    @GetMapping("/fetchAllFunctionalTitle")
    public Response getAllFunctionalTitle() throws BadRequestException {
        return functionalTitleService.getAllFunctionalTitle();
    }

    @GetMapping("/getFunctionalTitle/{empFuncTitleId}")
    public Response getFunctionalTitle(@PathVariable Long empFuncTitleId) throws BadRequestException {
        return functionalTitleService.getFunctionalTitle(empFuncTitleId);
    }

    @PostMapping("/add")
    public Response addFunctionalTitle(@RequestBody FunctionalTitleRequest request) throws BadRequestException {
        return functionalTitleService.addFunctionalTitle(request);
    }

    @PostMapping("/update")
    public Response updateFunctionalTitle(@RequestBody FunctionalTitleRequest request) throws BadRequestException {
        return functionalTitleService.updateFunctionalTitle(request);
    }
}
