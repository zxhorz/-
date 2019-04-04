package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.Service.MyInfoService;
import com.zxh.dormMG.domain.Student;
import com.zxh.dormMG.domain.User;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.dto.UserDto;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<UserDto> getUser() {
        Session session = SecurityUtils.getSubject().getSession();
        String userId = (String) session.getAttribute("signinId");
//        User user = userRepository.findUserByName(userId);
        User user = userRepository.findUserByName("31501105");
        UserDto userDto = new UserDto(user.getId(),user.getUsername(),user.getRoles().get(0).getRoleName());
        return ResultDtoFactory.toAck("S",userDto);
    }

}
