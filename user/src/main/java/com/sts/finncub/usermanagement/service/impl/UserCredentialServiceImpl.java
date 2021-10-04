package com.sts.finncub.usermanagement.service.impl;



import com.sts.finncub.usermanagement.service.UserCredentialService;
import com.sts.finncub.core.entity.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCredentialServiceImpl implements UserCredentialService {
    @Override
    public UserSession getUserData() {
        log.info("Fetching user data");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserSession){
            log.info("User data found");
            return ((UserSession) principal);
        }
        log.error("User data not found");
        return null;
    }




}
