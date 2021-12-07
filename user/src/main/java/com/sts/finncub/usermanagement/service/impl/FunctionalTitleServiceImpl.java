package com.sts.finncub.usermanagement.service.impl;

import com.sts.finncub.core.dto.EmployeeFunctionalTitleDto;
import com.sts.finncub.core.entity.EmployeeFunctionalTitle;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.core.repository.EmployeeFunctionalTitleRepository;
import com.sts.finncub.core.service.UserCredentialService;
import com.sts.finncub.usermanagement.request.FunctionalTitleRequest;
import com.sts.finncub.usermanagement.response.Response;
import com.sts.finncub.usermanagement.service.FunctionalTitleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FunctionalTitleServiceImpl implements FunctionalTitleService {

    private final EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository;

    private final UserCredentialService userCredentialService;

    @Autowired
    public FunctionalTitleServiceImpl(EmployeeFunctionalTitleRepository employeeFunctionalTitleRepository,
                                      UserCredentialService userCredentialService) {
        this.employeeFunctionalTitleRepository = employeeFunctionalTitleRepository;
        this.userCredentialService = userCredentialService;
    }



    @Override
    public Response getAllFunctionalTitle() {
        Response response = new Response();
        List<EmployeeFunctionalTitleDto> functionalTitleDtoList = new ArrayList<>();
        List<EmployeeFunctionalTitle> employeeFunctionalTitleList = employeeFunctionalTitleRepository.
                findByOrgId(userCredentialService.getUserSession().getOrganizationId());
        for (EmployeeFunctionalTitle employeeFunctionalTitle : employeeFunctionalTitleList) {
            EmployeeFunctionalTitleDto employeeFunctionalTitleDto = new EmployeeFunctionalTitleDto();
            BeanUtils.copyProperties(employeeFunctionalTitle, employeeFunctionalTitleDto);
            functionalTitleDtoList.add(employeeFunctionalTitleDto);
        }
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(functionalTitleDtoList);
        return response;
    }

    @Override
    public Response getFunctionalTitle(Long empFuncTitleId) throws BadRequestException {
        Response response = new Response();
        if (empFuncTitleId == null) {
            throw new BadRequestException("Invalid Employee Functional Title Id", HttpStatus.BAD_REQUEST);
        }
        EmployeeFunctionalTitle employeeFunctionalTitle = employeeFunctionalTitleRepository.
                findByOrgIdAndEmpFuncTitleId(userCredentialService.getUserSession().getOrganizationId(), empFuncTitleId);
        if (employeeFunctionalTitle == null) {
            throw new BadRequestException("No Data Found", HttpStatus.BAD_REQUEST);
        }
        EmployeeFunctionalTitleDto employeeFunctionalTitleDto = new EmployeeFunctionalTitleDto();
        BeanUtils.copyProperties(employeeFunctionalTitle, employeeFunctionalTitleDto);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        response.setData(employeeFunctionalTitleDto);
        return response;
    }

    @Override
    public Response addFunctionalTitle(FunctionalTitleRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || !StringUtils.hasText(request.getEmpFuncTitleName())
                || !StringUtils.hasText(request.getStatus())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeFunctionalTitle employeeFunctionalTitle = new EmployeeFunctionalTitle();
        employeeFunctionalTitle.setEmpFuncTitleName(request.getEmpFuncTitleName());
        employeeFunctionalTitle.setStatus(request.getStatus());
        employeeFunctionalTitle.setOrgId(userSession.getOrganizationId());
        employeeFunctionalTitle.setInsertedBy(userSession.getUserId());
        employeeFunctionalTitle.setInsertedOn(LocalDateTime.now());
        employeeFunctionalTitleRepository.save(employeeFunctionalTitle);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }

    @Override
    public Response updateFunctionalTitle(FunctionalTitleRequest request) throws BadRequestException {
        Response response = new Response();
        UserSession userSession = userCredentialService.getUserSession();
        if (request == null || request.getEmpFuncTitleId() == null || !StringUtils.hasText(request.getEmpFuncTitleName())
                || !StringUtils.hasText(request.getStatus())) {
            throw new BadRequestException("Invalid Request Parameters", HttpStatus.BAD_REQUEST);
        }
        EmployeeFunctionalTitle employeeFunctionalTitle = employeeFunctionalTitleRepository.
                findByOrgIdAndEmpFuncTitleId(userCredentialService.getUserSession().getOrganizationId(),
                        request.getEmpFuncTitleId());
        if (employeeFunctionalTitle == null) {
            throw new BadRequestException("No Data Found", HttpStatus.BAD_REQUEST);
        }
        employeeFunctionalTitle.setEmpFuncTitleName(request.getEmpFuncTitleName());
        employeeFunctionalTitle.setStatus(request.getStatus());
        employeeFunctionalTitle.setUpdatedBy(userSession.getUserId());
        employeeFunctionalTitle.setUpdatedOn(LocalDateTime.now());
        employeeFunctionalTitleRepository.save(employeeFunctionalTitle);
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK);
        response.setMessage("Transaction completed successfully.");
        return response;
    }
}
