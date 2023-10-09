package com.sts.finncub.usermanagement.util;

import com.sts.finncub.core.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaintainPasswordHistory {

    @Value("${old.password.count}")
    private Integer oldPasswordCount;

    public String maintainOldPasswordHistory(String oldPasswordList[], String oldPassword, String PASSWORD_SEPARATOR, String password) throws BadRequestException {
//          Maintain old passwords
        if (oldPasswordList.length < oldPasswordCount) {
            oldPassword = oldPassword + PASSWORD_SEPARATOR + password;
        } else {
            StringBuilder updatedOldPassword = new StringBuilder();
            for (int i = 1; i < oldPasswordList.length; i++) {
                if (updatedOldPassword.length() == 0) {
                    updatedOldPassword = new StringBuilder(oldPasswordList[i]);
                } else {
                    updatedOldPassword.append(PASSWORD_SEPARATOR).append(oldPasswordList[i]);
                }
            }
            oldPassword = updatedOldPassword + PASSWORD_SEPARATOR + password;
        }
        return oldPassword;
    }
}