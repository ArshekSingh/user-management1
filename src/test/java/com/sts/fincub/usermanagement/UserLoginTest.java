package com.sts.fincub.usermanagement;

import com.sts.fincub.usermanagement.response.LoginResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
@RunWith(SpringRunner.class)
public class UserLoginTest {

    private final LoginResponse expectedResponse = new LoginResponse("Some","Some",new ArrayList<>());



    @Test
    public void loginSuccessfulTest(){

    }

}
