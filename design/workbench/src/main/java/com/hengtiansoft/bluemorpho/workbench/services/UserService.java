package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.zefer.css.n;

import com.hengtiansoft.bluemorpho.workbench.domain.User;
import com.hengtiansoft.bluemorpho.workbench.dto.LoginDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.enums.UserState;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.smtp.Authentication;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PasswordUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.RSAUtils;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a>
 * Date: Jan 3, 2019 4:16:09 PM
 */
@Service
public class UserService {
	private static final Logger LOGGER = Logger.getLogger(UserService.class);
    private static final String LOCALPORT = ":8081";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PortUtil portUtil;
	public ResultDto<String> forget(String userName) {
        ResultDto<String> resultDto = new ResultDto<String>();
        boolean exists = checkUserExists(userName);
        if (!exists) {
            resultDto.setCode("E");
            resultDto.setData("The user is non-existent.");
            return resultDto;
        } else {
            resultDto.setData(null);
            // 4位激活码
            String activationCode = PasswordUtil.generateRandomString(4);
            // 发送密码和激活码到注册邮箱
            try {
                sendChangePassword(activationCode, userName);
            } catch (Exception e) {
                resultDto.setCode("E");
                resultDto.setData("Send activation email exception!");
                return resultDto;
            }
            User user = userRepository.findUserByName(userName);
            user.setActivationCode(PasswordUtil.MD5(activationCode));
            user.setTime("0");
            userRepository.save(user);
            resultDto.setCode("S");
            resultDto.setData("Activation email was successfully sended, please check in your email in 30 minutes!");
            return resultDto;
        }
    }

	public ResultDto<String> register(String userName) {
		ResultDto<String> resultDto = new ResultDto<String>();
        User user = userRepository.findUserByName(userName);
        boolean exists = (user != null);
        if (exists && user.getState().equals(UserState.ACTIVE.getState())) {
            resultDto.setCode("E");
            resultDto.setData("The user already exists.");
        } else {
            resultDto.setData(null);
            if (exists)
                ;
            else
                user = new User();
            String activationCode = PasswordUtil.generateRandomString(4);
            // 8位密码
            String password = PasswordUtil.generateRandomString(8);
            user.setUsername(userName);
            user.setPassword(PasswordUtil.MD5(password));
            user.setActivationCode(PasswordUtil.MD5(activationCode));
            user.setState(UserState.NON_ACTIVE.getState());
            // 设置初始时间为0，时间超过24后删除数据
            user.setTime("0");
            userRepository.save(user);
            try {
                sendPasswordAndActivationCode(activationCode, userName);
            } catch (Exception e) {
                resultDto.setCode("E");
                resultDto.setData("Send activation email exception!");
                return resultDto;
            }
            // 发送密码和激活码到注册邮箱
            resultDto.setCode("S");
            if (exists)
                resultDto.setData("The user has been registered, please activate your account in your email!");
            else
                resultDto.setData("The user was successfully registered, please activate your account in your email!");
        }
        return resultDto;
	}

	public ModelAndView activeAccount(String activationCode, String account) {

        User user = checkActivationCode(account, activationCode);
        String token = PasswordUtil.generateRandomString(16);
        if (user != null) {
            user.setState(UserState.ACTIVE.getState());
            user.setActivationCode(PasswordUtil.MD5(token));
            userRepository.save(user);
        }
        return new ModelAndView("redirect:/login-module/reset.html?token=" + token);
    }
	
    public User checkActivationCode(String email, String activationCode) {
        // TODO Auto-generated method stub
        User user = userRepository.findUserByName(email);
        if (user == null)
            return user;
        String time = user.getTime();
        if(time == null)
            return null;
        if (user.getState().equals(UserState.ACTIVE.getState()) && Integer.valueOf(time) > 30){
            user.setActivationCode(null);
            user.setTime(null);
            userRepository.save(user);
            return null;
        }
        if (user.getActivationCode().equals(PasswordUtil.MD5(activationCode)))
            return user;
        else
            return null;

    }

    public ResultDto<String> changePassword(String userName, String newPassword) {
        User user = userRepository.findUserByName(userName);
        if (user != null) {
            user.setPassword(PasswordUtil.MD5(newPassword));
            user.setState(UserState.ACTIVE.getState());
            user.setActivationCode(null);
            user.setTime(null);
            userRepository.save(user);
            return ResultDtoFactory.toAck("S","Password successfully changed");
        }
        else{
            return ResultDtoFactory.toAck("F","Password failed to change");
        }
    }

    public boolean checkCaptcha(String captcha,HttpServletRequest request){
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("captcha");
        if(captcha == null)
            return false;
        if (!sessionCode.equalsIgnoreCase(captcha)) {
            return false;
        }
        return true;
    }

    public ResultDto<User> getMessage(String token) {
        try {
            if(token.split("[?]").length != 2)
                return ResultDtoFactory.toAck("N");
            token = token.split("[?]")[1];
            token = token.split("=")[1];
            User user = userRepository.findUserByActivationCode(PasswordUtil.MD5(token));
            if (user != null) {
                user.setActivationCode(null);
                user.setTime(null);
                return ResultDtoFactory.toAck("S",user);
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e);
        }
        return ResultDtoFactory.toAck("F");
    }

    public boolean checkUserExists(String userName) {
    	User find = userRepository.findUserByName(userName);
    	if (find != null) {
    		return true;
    	}
    	return false;
    }


    private void sendPasswordAndActivationCode(String activationCode, String toEmail) throws MessagingException {
    	Properties props = new Properties();
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(FilePathUtil.getSmtpConfigPath()));
    		props.load(br);
    	} catch(Exception e) {
    		LOGGER.error("read smtp properties file error.");;
    	}
    	
    	String fromUserName = props.getProperty("username");
    	String fromPassword = props.getProperty("password");
    	String transportProtocol = props.getProperty("mail.transport.protocol");
    	String smtpHost = props.getProperty("mail.smtp.host");
    
    	Authentication authentication = new Authentication(fromUserName, fromPassword); 
    	Session mailSession = Session.getDefaultInstance(props, authentication);
    
    	Message msg = new MimeMessage(mailSession);
    	msg.setFrom(new InternetAddress(fromUserName));
    	msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    	msg.setSubject("BlueMorpho Workbench Activation Email");
        StringBuffer content = new StringBuffer();
        content.append("<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply. ");
        content.append("<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://");
        content.append(portUtil.getWbServerIp() + LOCALPORT);
        content.append("/login/activeAccount?activationCode=");
        content.append(activationCode);
        content.append("&account=");
        content.append(toEmail);
        content.append("'>http://");
        content.append(portUtil.getWbServerIp() + LOCALPORT);
        content.append("/activeAccount</a></h3>");
//        "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply. "
//        + "<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://"+portUtil.getWbServerIp() + LOCALPORT + "/login/activeAccount?activationCode="
//        + activationCode
//        + "&account="
//        + toEmail
//        + "'>http://"+ portUtil.getWbServerIp() + LOCALPORT + "/activeAccount</a></h3>"
    	msg.setContent(content.toString(), "text/html;charset=UTF-8");
    	msg.saveChanges();
    	Transport transport = mailSession.getTransport(transportProtocol);
    	transport.connect(smtpHost, fromUserName, fromPassword); 
    	transport.sendMessage(msg, msg.getAllRecipients());
    	transport.close();
    }


    private void sendChangePassword(String activationCode, String toEmail) throws MessagingException {
        Properties props = new Properties();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FilePathUtil.getSmtpConfigPath()));
            props.load(br);
        } catch(Exception e) {
            LOGGER.error("read smtp properties file error.");
        }
        
        String fromUserName = props.getProperty("username");
        String fromPassword = props.getProperty("password");
        String transportProtocol = props.getProperty("mail.transport.protocol");
        String smtpHost = props.getProperty("mail.smtp.host");
    
        Authentication authentication = new Authentication(fromUserName, fromPassword); 
        Session mailSession = Session.getDefaultInstance(props, authentication);
    
        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(fromUserName));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("BlueMorpho Workbench Activation Email");
        StringBuffer content = new StringBuffer();
        content.append("<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply.");
        content.append("<br/> Please click on the link below to reset your own password!</h1><h3><a href='http://");
        content.append(portUtil.getWbServerIp() + LOCALPORT);
        content.append("/login/resetPassword?activationCode=");
        content.append(activationCode);
        content.append("&account=");
        content.append(toEmail);
        content.append("'>http://");
        content.append(portUtil.getWbServerIp() + LOCALPORT);
        content.append("/resetPassword</a></h3>");
//        "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply."
//        + "<br/> Please click on the link below to reset your own password!</h1><h3><a href='http://"+portUtil.getWbServerIp() + LOCALPORT + "/login/resetPassword?activationCode="
//        + activationCode
//        + "&account="
//        + toEmail
//        + "'>http://"+ portUtil.getWbServerIp() + LOCALPORT + "/resetPassword</a></h3>"
        msg.setContent(content.toString(), "text/html;charset=UTF-8");
        msg.saveChanges();
    
        Transport transport = mailSession.getTransport(transportProtocol);
        transport.connect(smtpHost, fromUserName, fromPassword); 
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    public void decryptLoginDto(LoginDto loginDto){
        loginDto.setUserName(RSAUtils.decryptBase64(loginDto.getUserName()));
        loginDto.setPassword(RSAUtils.decryptBase64(loginDto.getPassword()));
        loginDto.setCaptcha(RSAUtils.decryptBase64(loginDto.getCaptcha()));
        loginDto.setNewPassword(RSAUtils.decryptBase64(loginDto.getNewPassword()));
    }

}
