package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.hengtiansoft.bluemorpho.workbench.domain.User;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ResultDtoFactory;
import com.hengtiansoft.bluemorpho.workbench.enums.UserState;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.smtp.Authentication;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.OperationLogger;
import com.hengtiansoft.bluemorpho.workbench.util.PasswordUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a>
 * Date: Jan 3, 2019 4:16:09 PM
 */
@Service
public class UserService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class);
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PortUtil portUtil;
	public boolean checkUserExists(String userName) {
		User find = userRepository.findUserByName(userName);
		if (find != null) {
			return true;
		}
		return false;
	}

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
            User user = userRepository.findUserByName(userName);
            user.setActivationCode(PasswordUtil.MD5(activationCode));
            user.setTime(OperationLogger.getCurrentTime());
            userRepository.save(user);
            // 发送密码和激活码到注册邮箱
            try {
                sendChangePassword(activationCode, userName);
            } catch (Exception e) {
                resultDto.setCode("E");
                resultDto.setData("Send activation email exception!");
                return resultDto;
            }
            resultDto.setCode("S");
            resultDto.setData("Activation email was successfully sended, please check in your email in 15 minutes!");
            return resultDto;
        }
    }
	
	
	public ResultDto<String> register(String userName) {
		ResultDto<String> resultDto = new ResultDto<String>();
		boolean exists = checkUserExists(userName);
		if (exists) {
		    User user = userRepository.findUserByName(userName);
		    String state = user.getState();
		    if(state.equals(UserState.ACTIVE.toString())){
    			resultDto.setCode("E");
    			resultDto.setData("The user already exists.");
		    }
		    else{
		        resultDto.setData(null);
		        String activationCode = PasswordUtil.generateRandomString(4);
	            // 8位密码
	            String password = PasswordUtil.generateRandomString(8);
	            user.setPassword(PasswordUtil.MD5(password));
	            user.setActivationCode(PasswordUtil.MD5(activationCode));
	            user.setState(UserState.NON_ACTIVE.getState());
	            user.setTime(OperationLogger.getCurrentTime());
	            userRepository.save(user);
	            // 发送密码和激活码到注册邮箱
	            try {
	                sendPasswordAndActivationCode(activationCode, password, userName);
	            } catch(Exception e) {
	                resultDto.setCode("E");
	                resultDto.setData("Send activation email exception!");
	                return resultDto;
	            }
	            resultDto.setCode("S");
	            resultDto.setData("The user has been registered, please activate your account in your email!");
		    }
			return resultDto;
		} else {
			resultDto.setData(null);
			// 4位激活码
			String activationCode = PasswordUtil.generateRandomString(4);
			// 8位密码
			String password = PasswordUtil.generateRandomString(8);
			User user = new User();
			user.setUsername(userName);
			user.setPassword(PasswordUtil.MD5(password));
			user.setActivationCode(PasswordUtil.MD5(activationCode));
			user.setState(UserState.NON_ACTIVE.getState());
            user.setTime(OperationLogger.getCurrentTime());
			userRepository.save(user);
			// 发送密码和激活码到注册邮箱
			try {
				sendPasswordAndActivationCode(activationCode, password, userName);
			} catch(Exception e) {
				resultDto.setCode("E");
				resultDto.setData("Send activation email exception!");
				return resultDto;
			}
			resultDto.setCode("S");
			resultDto.setData("The user was successfully registered, please activate your account in your email!");
			return resultDto;
		}
	}

	private void sendPasswordAndActivationCode(String activationCode, String password, String toEmail) throws MessagingException {
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
		msg.setContent("<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply. "
				+ "<br/>password : "
				+ password
				+ ".<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://"+portUtil.getWbServerIp() + ":8081/login/activeAccount?activationCode="
				+ activationCode
				+ "&account="
				+ toEmail
				+ "'>http://"+ portUtil.getWbServerIp() + ":8081/activeAccount</a></h3>", "text/html;charset=UTF-8");
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
	        msg.setContent("<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply."
	                + "<br/>Activation code is ："
	                + activationCode+"</h1>", "text/html;charset=UTF-8");
	        msg.saveChanges();

	        Transport transport = mailSession.getTransport(transportProtocol);
	        transport.connect(smtpHost, fromUserName, fromPassword); 
	        transport.sendMessage(msg, msg.getAllRecipients());
	        transport.close();
	    }

	public ModelAndView activeAccount(String activationCode, String account) {
		User user = userRepository.findUserByActivationCode(PasswordUtil.MD5(activationCode));
		if (user != null) {
			user.setState(UserState.ACTIVE.getState());
			userRepository.save(user);
		}
		return new ModelAndView("redirect:/login-module/login3.html");
	}
	
    public ResultDto<String> changePassword(String userName, String newPassword) {
        User user = userRepository.findUserByName(userName);
        if (user != null) {
            user.setPassword(PasswordUtil.MD5(newPassword));
            user.setState(UserState.ACTIVE.getState());
            userRepository.save(user);
            return ResultDtoFactory.toAck("S","Password successfully changed");
        }
        else{
            return ResultDtoFactory.toAck("F","Password failed to change");
        }
    }

    public ResultDto<String> checkForgotActivationCode(String email, String activationCode) {
        // TODO Auto-generated method stub
        User user = userRepository.findUserByName(email);
        String time = user.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            Date currentDate = new Date();
            long diff = currentDate.getTime() - date.getTime();
            long min = diff /(60*1000);
            if(min > 15)
                return ResultDtoFactory.toAck("F","Activation code is overdue");
            if(user.getActivationCode().equals(PasswordUtil.MD5(activationCode)))
                return ResultDtoFactory.toAck("S");
            else
                return ResultDtoFactory.toAck("F","Activation code not correct");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return ResultDtoFactory.toAck("F","Record time error");
        }
    }

    public boolean checkCaptcha(String captcha,HttpServletRequest request){
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("captcha");
        if (!sessionCode.equalsIgnoreCase(captcha)) {
            return false;
        }
        return true;
    }
}
