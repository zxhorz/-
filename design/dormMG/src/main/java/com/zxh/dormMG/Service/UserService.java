package com.zxh.dormMG.Service;

import com.zxh.dormMG.Domain.User;
import com.zxh.dormMG.Repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    private static final Logger logger = Logger.getLogger(StudentService.class);

    @Autowired
    private UserRepository userRepository;

    public User getUser(String username){
        User user = userRepository.findUserByName(username);
        return user;
    }
}
