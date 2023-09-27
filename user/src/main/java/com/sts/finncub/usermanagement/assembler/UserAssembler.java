package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.UserDetailDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAssembler {

    public List<UserDetailDto> assembleUser(List<Object[]> userBranchMapping) {
        return userBranchMapping.stream().map(this::assembleUser).collect(Collectors.toList());
    }

    public UserDetailDto assembleUser(Object[] user) {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setName((String) user[1]);
        userDetailDto.setUserId((String) user[0]);
        return userDetailDto;
    }
}
