package com.sts.finncub.usermanagement.assembler;

import com.sts.finncub.core.dto.UserDetailDto;
import com.sts.finncub.core.entity.User;
import com.sts.finncub.core.entity.UserBranchMapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAssembler {

    public List<UserDetailDto> assembleUser(List<UserBranchMapping> userBranchMapping) {
        return userBranchMapping.stream().map(this::assembleUser).collect(Collectors.toList());
    }

    public UserDetailDto assembleUser(UserBranchMapping userBranchMapping) {
        User user = userBranchMapping.getUser();
        if (user != null) {
            UserDetailDto userDetailDto = new UserDetailDto();
            userDetailDto.setName(user.getName());
            userDetailDto.setUserId(user.getUserId());
            return userDetailDto;
        }
        return null;
    }
}
