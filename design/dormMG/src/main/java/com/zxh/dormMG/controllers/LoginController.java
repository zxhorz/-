package com.zxh.dormMG.controllers;

import com.zxh.dormMG.Service.LoginService;
import com.zxh.dormMG.domain.Role;
import com.zxh.dormMG.domain.User;
import com.zxh.dormMG.dto.LoginDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.utils.PasswordUtil;
import com.zxh.dormMG.utils.RSAUtils;
import com.zxh.dormMG.utils.ValidateCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    //post登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> login(@Valid LoginDto loginDto, BindingResult result, HttpServletRequest request) {
        loginService.decryptLoginDto(loginDto);

        Subject currentUser = SecurityUtils.getSubject();
        String captcha = loginDto.getCaptcha();
        if (!loginService.checkCaptcha(captcha, request))
            return ResultDtoFactory.toAck("F", "Captcha wrong");
        String userName = loginDto.getUserName();
        String password = loginDto.getPassword();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, PasswordUtil.MD5(password));
        token.setRememberMe(true);
        try {
            currentUser.login(token);
            Session s = currentUser.getSession();
            s.setAttribute("signinId", userName);
            // ModelAndView modelAndView = new ModelAndView("redirect:/index.html");
            // return modelAndView;
            return ResultDtoFactory.toAck("S", userName);
        } catch (Exception e) {
            token.clear();
            // ModelAndView modelAndView = new ModelAndView("redirect:/static/login-module/login1.html");
            // return modelAndView;
            return ResultDtoFactory.toAck("F", "userName/password not correct or the account is not activated!");
        }
    }


    //退出的时候是get请求，主要是用于退出
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    //登出
    @RequestMapping(value = "/logout")
    public String logout() {
        return "logout";
    }

    //错误页面展示
    @RequestMapping(value = "/error", method = RequestMethod.POST)
    public String error() {
        return "error ok!";
    }

    //数据初始化
    @RequestMapping(value = "/addUser")
    public String addUser(@RequestBody Map<String, Object> map) {
        User user = loginService.addUser(map);
        return "addUser is ok! \n" + user;
    }

    //角色初始化
    @RequestMapping(value = "/addRole")
    public String addRole(@RequestBody Map<String, Object> map) {
        Role role = loginService.addRole(map);
        return "addRole is ok! \n" + role;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> register(@RequestParam("email") String email) {
        return loginService.register(email);
    }

    @RequestMapping(value = "/activeAccount", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView activeAccount(@RequestParam("activationCode") String activationCode, @RequestParam("account") String account) {
        return loginService.activeAccount(activationCode, account);
    }

    //注解的使用
    @RequiresRoles("admin")
    @RequiresPermissions("create")
    @RequestMapping(value = "/create")
    public String create() {
        return "Create success!";
    }


    @RequestMapping(value = "/forget", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> forget(@RequestParam("email") String email) {
        return loginService.forget(email);
    }

    @RequestMapping(value = "/getKey", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto<String> getKey() {
        String publicKey = RSAUtils.generateBase64PublicKey();
        return ResultDtoFactory.toAck("S", publicKey);
    }

    @RequestMapping(value = "/getMessage", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<User> getMessage(@RequestParam("token") String token) {
        return loginService.getMessage(token);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> changePassword(@Valid LoginDto loginDto, BindingResult result) {
        loginService.decryptLoginDto(loginDto);
        Subject currentUser = SecurityUtils.getSubject();
        String userName = loginDto.getUserName();
        String password = loginDto.getPassword();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        token.setRememberMe(true);
        try {
            currentUser.login(token);
            String newPassword = loginDto.getNewPassword();
            if (password != null)
                loginService.changePassword(userName, newPassword);
            Session s = currentUser.getSession();
            s.setAttribute("signinId", userName);
            // ModelAndView modelAndView = new ModelAndView("redirect:/index.html");
            return ResultDtoFactory.toAck("S", userName);
        } catch (Exception e) {
            token.clear();
            // ModelAndView modelAndView = new ModelAndView("redirect:/static/login-module/login1.html");
            return ResultDtoFactory.toAck("F", e.getMessage());
        }
    }

    @RequestMapping(value = "/validateCode", method = RequestMethod.GET)
    public String validateCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("image/jpeg");
        // 禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();

        ValidateCode vCode = new ValidateCode(120, 40, 5, 100);
        session.setAttribute("captcha", vCode.getCode());
        vCode.write(response.getOutputStream());
        return null;
    }

}