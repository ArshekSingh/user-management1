package com.sts.fincub.usermanagement.entity;

import com.sts.fincub.usermanagement.response.LoginResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = User.Columns.TABLE_NAME)
public class User  implements Serializable {
     interface Columns{
        String TABLE_NAME = "USER_MASTER";

        String USER_NAME = "USER_NAME";
        String EMAIL_ID = "EMAIL_ID";
        String USER_TYPE = "USER_TYPE";
        String PASSWORD = "PASSWORD";
        String ACTIVE= "ACTIVE";
        String USER_ID = "USER_ID";
        String USER_VALIDATED= "USER_VALIDATED";
        String MOBILE_NUMBER = "MOBILE_NUMBER";
        String OTP_VALIDATED = "OTP_VALIDATED";
        String PASSWORD_RESET_DATE = "PASSWORD_RESET_DATE";
        String DISABLED_ON = "DISABLED_ON";
        String APPROVED_ON = "APPROVED_ON";
        String APPROVED_BY = "APPROVED_BY";
        String INSERTED_ON = "INSERTED_ON";
        String INSERTED_BY = "INSERTED_BY";
        String UPDATED_ON ="UPDATED_ON";
        String UPDATED_BY = "UPDATED_BY";
    }

    private final static long serialVersionUID = -6524768694427900622L;


    @Column(name =  Columns.USER_NAME)
    private String name;

    @Id
    @Column(name = Columns.USER_ID)
    private String userId;

    @Column(name =  Columns.EMAIL_ID)
    private String email;

    @Column(name=  Columns.USER_TYPE)
    private String type;

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles = new HashSet<>();

    @Column(name =  Columns.PASSWORD)
    private String password;

    @Column(name = Columns.ACTIVE)
    private boolean isActive;


    @Column(name = Columns.USER_VALIDATED)
    private Character isUserValidated;

    @Column(name = Columns.MOBILE_NUMBER)
    private String mobileNumber;

    @Column(name = Columns.OTP_VALIDATED)
    private Character isOtpValidated;

    @Column(name = Columns.PASSWORD_RESET_DATE)
    private LocalDate passwordResetDate;

    @Column(name = Columns.DISABLED_ON)
    private LocalDate disabledOn;

    @Column(name = Columns.APPROVED_ON)
    private LocalDate approvedOn;

    @Column(name = Columns.APPROVED_BY)
    private LocalDate approvedBy;

    @Column(name = Columns.INSERTED_ON)
    private LocalDate insertedOn;

    @Column(name = Columns.INSERTED_BY)
    private String insertedBy;

    @Column(name = Columns.UPDATED_ON)
    private LocalDate updatedOn;

    @Column(name = Columns.UPDATED_BY)
    private String updatedBy;

    public void setPassword(PasswordEncoder passwordEncoder,String password){
        this.password = passwordEncoder.encode(password);
    }

    public boolean isPasswordCorrect(String password){
       return BCrypt.checkpw(password,this.password);
    }

    public UserSession toSessionObject(){
        UserSession userSession = new UserSession();
        userSession.setEmail(email);
//        userSession.setRoles(roles);
        userSession.setName(name);
        userSession.setType(type);
        userSession.setUserId(userId);
        return userSession;
    }



}
