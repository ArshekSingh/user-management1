package com.sts.fincub.usermanagement.service.impl;

import com.sts.fincub.usermanagement.config.UserDetailsImpl;
import com.sts.fincub.usermanagement.entity.User;
import com.sts.fincub.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByName(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }
}
