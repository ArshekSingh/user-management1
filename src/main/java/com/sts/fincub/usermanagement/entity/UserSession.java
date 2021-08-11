package com.sts.fincub.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RedisHash("USER_SESSION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession implements Serializable {
    interface Columns{
        String ID = "ID";
        String NAME = "NAME";
        String EMAIL = "EMAIL";
        String USER_TYPE = "USER_TYPE";
    }

    private final static long serialVersionUID = -6524768694427900654L;

    private String id;


    private String name;


    private String email;


    private String type;


    private Set<Role> roles = new HashSet<>();


}
