package com.sts.fincub.usermanagement.request;

import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class    SignupRequest {
    String name;
    String userId;
    String password;
    String role;
    String email;
    String mobile;
    String userType;


    public User toDAO(PasswordEncoder passwordEncoder){
        User user = new User();
        user.setActive(true);
        user.setName(name);
        user.setPassword(passwordEncoder,password);
        user.setEmail(email);
        user.setUserId(userId);
        user.setMobileNumber(mobile);
        user.setType(userType);
        user.setPasswordResetDate(LocalDate.now());
        user.setInsertedOn(LocalDate.now());
        user.setInsertedBy(name);
        return user;
    }


    public void validate() throws BadRequestException{
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
        if(userId == null || userId.isEmpty()){
            buffer.append("Field : userId is mandatory, ");
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
            buffer.append("Field : password is mandatory");
            isValid = false;
        }

        if(!isValid){
            throw new BadRequestException(buffer.toString(), HttpStatus.BAD_REQUEST);
        }




    }


}
