package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.BranchEmployeeDto;
import com.sts.finncub.core.dto.UserDetailDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAssembler {

    public List<UserDetailDto> assembleUser(List<BranchEmployeeDto> userBranchMapping) {
        return userBranchMapping.stream().map(this::assembleUser).collect(Collectors.toList());
    }

    public UserDetailDto assembleUser(BranchEmployeeDto user) {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setName(user.getUserName().substring(8));
        userDetailDto.setUserId(user.getUserId());
        return userDetailDto;
    }
}
