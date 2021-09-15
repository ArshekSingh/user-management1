package com.sts.fincub.usermanagement.request;

import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.entity.enums.UserType;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class  SignupRequest {
    String name;
    String password;
    List<Long> roleList;
    String email;
    String mobile;
    String userType;




    public void validate() throws BadRequestException{
        log.info("Validating signup request data");
        boolean isValid = true;
        StringBuffer buffer = new StringBuffer();
        if (name == null || name.isEmpty()) {
            buffer.append("Field : name is mandatory, ");
            isValid = false;
        }
        if(email == null || email.isEmpty()){
            buffer.append("Field : email is mandatory, ");
            isValid = false;
        }
        if(password == null || password.isEmpty()){
            buffer.append("Field : password is mandatory");
            isValid = false;
        }

        if(password == null || password.isEmpty()){
            buffer.append("Field : password is mandatory");
            isValid = false;
        }

        if(userType == null || userType.isEmpty()){
            buffer.append("Field : userType is mandatory");
            isValid = false;
        }else if (!(UserType.EMP.name().equals(userType))){
            buffer.append("Invalid value for UserType - Accepted value => (EMP)");
            isValid = false;
        }



        if(!isValid){
            throw new BadRequestException(buffer.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    public boolean hasRoles(){
        return roleList != null && !roleList.isEmpty();
    }


}
