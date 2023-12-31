package com.sts.finncub.usermanagement.request;

import com.sts.finncub.core.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequest {
    String name;
    //    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]*$",message = "Password should contain alpha-numeric values")
//    @Size(min = 8,max = 16, message = "Password length should at least 8 and maximum of 16 characters")
    String password;
    List<Long> roleList;
    String email;
    String mobile;
    String userType;

    public void validate() throws BadRequestException {
        log.info("Validating signup request data , email : {} , userType : {}", email, userType);
        boolean isValid = true;
        StringBuilder stringBuilder = new StringBuilder();
        if (name == null || name.isEmpty()) {
            stringBuilder.append("Field : name is mandatory, ");
            isValid = false;
        }
        if (email == null || email.isEmpty()) {
            stringBuilder.append("Field : email is mandatory, ");
            isValid = false;
        }
        if (password == null || password.isEmpty()) {
            stringBuilder.append("Field : password is mandatory");
            isValid = false;
        }
        if (password == null || password.isEmpty()) {
            stringBuilder.append("Field : password is mandatory");
            isValid = false;
        }
        if (userType == null || userType.isEmpty()) {
            stringBuilder.append("Field : userType is mandatory");
            isValid = false;
//        }else if (!(UserType.EMP.name().equals(userType))){
//            stringBuilder.append("Invalid value for UserType - Accepted value => (EMP)");
//            isValid = false;
//        }
        }
        if (!isValid) {
            throw new BadRequestException(stringBuilder.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    public boolean hasRoles() {
        return roleList != null && !roleList.isEmpty();
    }
}