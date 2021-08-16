package com.sts.fincub.usermanagement.request;

import com.sts.fincub.usermanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

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


    public User toDAO(PasswordEncoder passwordEncoder){
        User user = new User();
        user.setActive(true);
        user.setName(name);
        user.setPassword(passwordEncoder,password);
        user.setEmail(email);
        return user;
    }


}
