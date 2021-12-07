package com.sts.finncub.usermanagement.service;

import com.sts.finncub.core.exception.BadRequestException;
import com.sts.finncub.usermanagement.request.FunctionalTitleRequest;
import com.sts.finncub.usermanagement.response.Response;

public interface FunctionalTitleService {
    Response getAllFunctionalTitle();

    Response getFunctionalTitle(Long empFuncTitleId) throws BadRequestException;

    Response addFunctionalTitle(FunctionalTitleRequest request) throws BadRequestException;

    Response updateFunctionalTitle(FunctionalTitleRequest request) throws BadRequestException;
}
