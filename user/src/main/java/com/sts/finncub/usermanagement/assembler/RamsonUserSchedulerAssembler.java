package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.entity.BranchMaster;
import com.sts.finncub.core.entity.VwFoUserExport;
import com.sts.finncub.core.repository.BranchMasterRepository;
import com.sts.finncub.core.util.DateTimeUtil;
import com.sts.finncub.usermanagement.request.RamsonUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RamsonUserSchedulerAssembler {
    @Autowired
    private BranchMasterRepository branchMasterRepository;

    public RamsonUserRequest prepareRequest(VwFoUserExport vwFoUserExport) {
        RamsonUserRequest request = new RamsonUserRequest();
        if (StringUtils.hasText(vwFoUserExport.getUserId())) {
            request.setUserId(vwFoUserExport.getUserId());
        }
        if (StringUtils.hasText(vwFoUserExport.getUserType())) {
            request.setType(vwFoUserExport.getUserType());
        }
        if (StringUtils.hasText(vwFoUserExport.getUserName())) {
            request.setName(vwFoUserExport.getUserName());
        }
        if (StringUtils.hasText(vwFoUserExport.getPassword())) {
            request.setPassword(vwFoUserExport.getPassword());
        }
        if (vwFoUserExport.getMobileNumber() != null) {
            request.setMobileNumber(vwFoUserExport.getMobileNumber().toString());
        }
        if (StringUtils.hasText(vwFoUserExport.getEmailId())) {
            request.setEmail(vwFoUserExport.getEmailId());
        }
        if (StringUtils.hasText(vwFoUserExport.getActive())) {
            request.setActive(vwFoUserExport.getActive());
        }
        request.setOrgId(1L);
        request.setRoleId(222L);
        if (StringUtils.hasText(vwFoUserExport.getBranchCode())) {
            request.setBranchCode(vwFoUserExport.getBranchCode());
        }
        BranchMaster branchMaster = branchMasterRepository.findByBranchCodeAndOrgId(vwFoUserExport.getBranchCode(), request.getOrgId());
        if (branchMaster != null) {
            request.setBranchId(branchMaster.getBranchId());
        }
        if (vwFoUserExport.getInsertedOn() != null) {
            request.setInsertedOn(DateTimeUtil.dateTimeToString(vwFoUserExport.getInsertedOn()));
        }
        if (StringUtils.hasText(vwFoUserExport.getInsertedBy())) {
            request.setInsertedBy(vwFoUserExport.getInsertedBy());
        }
        return request;
    }
}