package com.hengtiansoft.bluemorpho.workbench.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hengtiansoft.bluemorpho.workbench.dto.LoginDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.services.UserService;
import com.hengtiansoft.bluemorpho.workbench.util.PasswordUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ValidateCode;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 1:29:54 PM
 */

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> login(@Valid LoginDto loginDto, BindingResult result, HttpServletRequest request) {
        Subject currentUser = SecurityUtils.getSubject();
        String captcha = loginDto.getCaptcha();
        if(!userService.checkCaptcha(captcha, request))
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> register(@RequestParam("email") String email,@RequestParam("captcha") String captcha, HttpServletRequest request) {
        if(!userService.checkCaptcha(captcha, request)){
            ResultDto<String> resultDto = new ResultDto<String>();
            resultDto.setCode("E");
            resultDto.setData("Captcha Wrong!");
            return resultDto;
        }
        return userService.register(email);
    }

    @RequestMapping(value = "/activeAccount", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView activeAccount(@RequestParam("activationCode") String activationCode,
            @RequestParam("account") String account) {
        return userService.activeAccount(activationCode, account);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> changePassword(@Valid LoginDto loginDto, BindingResult result) {
        Subject currentUser = SecurityUtils.getSubject();
        String userName = loginDto.getUserName();
        String password = loginDto.getPassword();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, PasswordUtil.MD5(password));
        token.setRememberMe(true);
        try {
            currentUser.login(token);
            String newPassword = loginDto.getNewPassword();
            if (newPassword != null)
                userService.changePassword(userName, PasswordUtil.MD5(newPassword));
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

    @RequestMapping(value = "/forget", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> forget(@RequestParam("email") String email) {
        return userService.forget(email);
    }

    @RequestMapping(value = "/checkForgotActivationCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> checkForgotActivationCode(@RequestParam("email") String email,
            @RequestParam("activationCode") String activationCode) {
        return userService.checkForgotActivationCode(email, activationCode);
    }

    @RequestMapping(value = "/changeForgotPassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto<String> changeForgotPassword(@Valid LoginDto loginDto, BindingResult result) {
        String userName = loginDto.getUserName();
        String password = loginDto.getPassword();
        return userService.changePassword(userName, password);
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
