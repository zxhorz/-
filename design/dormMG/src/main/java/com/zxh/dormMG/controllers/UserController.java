package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Domain.Role;
import com.zxh.dormMG.Domain.User;
import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.Service.LoginService;
import com.zxh.dormMG.dto.Authority;
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

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<UserDto> getUser() {
        Session session = SecurityUtils.getSubject().getSession();
        String userId = (String) session.getAttribute("signinId");
        User user = userRepository.findUserByName(userId);
//        User user = userRepository.findUserByName("31501105");
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getRoles().get(0).getRoleName());
        return ResultDtoFactory.toAck("S", userDto);
    }

    @RequestMapping(value = "/getAuthority", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> getAuthority(@RequestParam("username") String username) {
        User user = userRepository.findUserByName(username);
        if (user == null ||user.getRoles().get(0).getRoleName().equals("root"))
            return ResultDtoFactory.toAck("F");
        else
            return ResultDtoFactory.toAck("S", user.getRoles().get(0).getRoleName());

    }

    @RequestMapping(value = "/updateAuthority", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> updateAuthority(Authority authority) {
        switch (authority.getAuthority()) {
            case "管理员":
                authority.setAuthority("admin");
                break;
            case "学生":
                authority.setAuthority("user");
                break;
            default:
                return ResultDtoFactory.toAck("F", "修改失败");
        }
        User user1 = userRepository.findUserByName(getUser().getData().getUserName());
        User user2 = userRepository.findUserByName(authority.getUsername());
        if (user2 == null)
            return ResultDtoFactory.toAck("F", "不存在此账号");
        if (user1.getRoles().get(0).getLevel() < user2.getRoles().get(0).getLevel())
            return ResultDtoFactory.toAck("F", "权限不足");
        Role role = loginService.updateRole(authority.getUsername(), authority.getAuthority());
        if (role != null)
            return ResultDtoFactory.toAck("S", "修改成功");
        else
            return ResultDtoFactory.toAck("F", "修改失败");
    }

}
