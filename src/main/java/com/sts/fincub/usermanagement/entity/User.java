package com.sts.fincub.usermanagement.entity;

import com.sts.fincub.usermanagement.response.LoginResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = User.Columns.TABLE_NAME)
public class User  implements Serializable {
     interface Columns{
        String TABLE_NAME = "USER_MASTER";
        String ID = "ID";
        String NAME = "NAME";
        String EMAIL = "EMAIL";
        String USER_TYPE = "USER_TYPE";
        String PASSWORD = "PASSWORD";
        String IS_ACTIVE= "IS_ACTIVE";
    }

    private final static long serialVersionUID = -6524768694427900622L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.ID)
    private Long id;

    @Column(name =  Columns.NAME)
    private String name;

    @Column(name =  Columns.EMAIL)
    private String email;

    @Column(name=  Columns.USER_TYPE)
    private String type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name =  Columns.PASSWORD)
    private String password;

    @Column(name = Columns.IS_ACTIVE)
    private boolean isActive;


    public void setPassword(PasswordEncoder passwordEncoder,String password){
        this.password = passwordEncoder.encode(password);
    }

    public boolean isPasswordCorrect(String password){
        return this.password.equalsIgnoreCase(password);
    }

    public UserSession toSessionObject(){
        UserSession userSession = new UserSession();
        userSession.setEmail(email);
        userSession.setRoles(roles);
        userSession.setName(name);
        userSession.setType(type);
        return userSession;
    }



}
