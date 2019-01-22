package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.RoleRepository;
import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.domain.Authentication;
import com.zxh.dormMG.domain.Permission;
import com.zxh.dormMG.domain.Role;
import com.zxh.dormMG.domain.User;
import com.zxh.dormMG.dto.LoginDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.dto.ResultDtoFactory;
import com.zxh.dormMG.enums.UserState;
import com.zxh.dormMG.utils.*;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@Transactional
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class);
    private static final String LOCALPORT = ":8888";

    @Autowired
    private PortUtil portUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private boolean checkUserExists(String userName) {
        User find = userRepository.findUserByName(userName);
        return find != null;
    }

    //添加用户
    public User addUser(Map<String, Object> map) {
        User user = new User();
        user.setUsername(map.get("username").toString());
        user.setPassword(map.get("password").toString());
        userRepository.save(user);
        return user;
    }

    //验证码校验
    public boolean checkCaptcha(String captcha, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("captcha");
        return captcha != null && sessionCode.equalsIgnoreCase(captcha);
    }

    //添加角色
    public Role addRole(Map<String, Object> map) {
        User user = userRepository.findOne(Long.valueOf(map.get("userId").toString()));
        Role role = new Role();
        role.setRoleName(map.get("roleName").toString());
        role.setUser(user);
        Permission permission1 = new Permission();
        permission1.setPermission("create");
        permission1.setRole(role);
        Permission permission2 = new Permission();
        permission2.setPermission("update");
        permission2.setRole(role);
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permission1);
        permissions.add(permission2);
        role.setPermissions(permissions);
        roleRepository.save(role);
        return role;
    }

    //查询用户通过用户名
    public User findByName(String name) {
        return userRepository.findUserByName(name);
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

    private User checkActivationCode(String email, String activationCode) {
        User user = userRepository.findUserByName(email);
        if (user == null)
            return null;
        String time = user.getTime();
        if (time == null)
            return null;
        if (user.getState().equals(UserState.ACTIVE.getState()) && Integer.valueOf(time) > 30) {
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

    public ResultDto<String> forget(String userName) {
        ResultDto<String> resultDto = new ResultDto<>();
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
        User user = userRepository.findUserByName(userName);
        boolean exists = (user != null);
        if (!checkEmail(userName)) {
            resultDto.setCode("E");
            resultDto.setData("Invalid Email");
            return resultDto;
        }
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

    public ResultDto<String> changePassword(String userName, String newPassword) {
        User user = userRepository.findUserByName(userName);
        if (user != null) {
            user.setPassword(PasswordUtil.MD5(newPassword));
            user.setState(UserState.ACTIVE.getState());
            user.setActivationCode(null);
            user.setTime(null);
            userRepository.save(user);
            return ResultDtoFactory.toAck("S", "Password successfully changed");
        } else {
            return ResultDtoFactory.toAck("F", "Password failed to change");
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
            long min = diff / (60 * 1000);
            if (min > 15)
                return ResultDtoFactory.toAck("F", "Activation code is overdue");
            if (user.getActivationCode().equals(PasswordUtil.MD5(activationCode)))
                return ResultDtoFactory.toAck("S");
            else
                return ResultDtoFactory.toAck("F", "Activation code not correct");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return ResultDtoFactory.toAck("F", "Record time error");
        }
    }

    public ResultDto<User> getMessage(String token) {
        try {
            if (token.split("[?]").length != 2)
                return ResultDtoFactory.toAck("N");
            token = token.split("[?]")[1];
            token = token.split("=")[1];
            User user = userRepository.findUserByActivationCode(PasswordUtil.MD5(token));
            if (user != null) {
                user.setActivationCode(null);
                user.setTime(null);
                return ResultDtoFactory.toAck("S", user);
            }
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e);
        }
        return ResultDtoFactory.toAck("F");
    }

    private void sendPasswordAndActivationCode(String activationCode, String toEmail) throws MessagingException {
        Properties props = new Properties();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FilePathUtil.getSmtpConfigPath()));
            props.load(br);
        } catch (Exception e) {
            logger.error("read smtp properties file error.");
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
        String content = "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply. " +
                "<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/login/activeAccount?activationCode=" +
                activationCode +
                "&account=" +
                toEmail +
                "'>http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/activeAccount</a></h3>";
        //        "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply. "
//        + "<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://"+portUtil.getWbServerIp() + LOCALPORT + "/login/activeAccount?activationCode="
//        + activationCode
//        + "&account="
//        + toEmail
//        + "'>http://"+ portUtil.getWbServerIp() + LOCALPORT + "/activeAccount</a></h3>"
        msg.setContent(content, "text/html;charset=UTF-8");
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
        } catch (Exception e) {
            logger.error("read smtp properties file error.");
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
        String content = "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply." +
                "<br/> Please click on the link below to reset your own password!</h1><h3><a href='http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/login/resetPassword?activationCode=" +
                activationCode +
                "&account=" +
                toEmail +
                "'>http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/resetPassword</a></h3>";
        //        "<h1>This is an official activation email from BlueMorpho Workbench,please dont't reply."
//        + "<br/> Please click on the link below to reset your own password!</h1><h3><a href='http://"+portUtil.getWbServerIp() + LOCALPORT + "/login/resetPassword?activationCode="
//        + activationCode
//        + "&account="
//        + toEmail
//        + "'>http://"+ portUtil.getWbServerIp() + LOCALPORT + "/resetPassword</a></h3>"
        msg.setContent(content, "text/html;charset=UTF-8");
        msg.saveChanges();

        Transport transport = mailSession.getTransport(transportProtocol);
        transport.connect(smtpHost, fromUserName, fromPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    private static boolean checkEmail(String email) {
//        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
//            logger.error("邮箱（" + email + "）校验未通过，格式不对!");
//            return false;
//        }
        String host;
        String hostName = email.split("@")[1];
        Record[] result;
        SMTPClient client = new SMTPClient();
        try {
            // 查找DNS缓存服务器上为MX类型的缓存域名信息
            Lookup lookup = new Lookup(hostName, Type.MX);
            lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL) {// 查找失败
                logger.error("邮箱（" + email + "）校验未通过，未找到对应的MX记录!");
                return false;
            } else {// 查找成功
                result = lookup.getAnswers();
            }
            // 尝试和SMTP邮箱服务器建立Socket连接
            for (Record aResult : result) {
                host = aResult.getAdditionalName().toString();
                logger.info("SMTPClient try connect to host:" + host);

                // 尝试Socket连接到SMTP服务器
                client.connect(host);
                // 查看响应码是否正常
                // 所有以2开头的响应码都是正常的响应
                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
                    // 断开socket连接
                    client.disconnect();
                } else {
                    logger.info("找到MX记录:" + hostName);
                    logger.info("建立链接成功：" + hostName);
                    break;
                }
            }
            logger.info("SMTPClient ReplyString:" + client.getReplyString());
            String emailSuffix = "163.com";
            String emailPrefix = "15382327056";
            String fromEmail = emailPrefix + "@" + emailSuffix;
            // 尝试和SMTP服务器建立连接,发送一条消息给SMTP服务器
            client.login(emailPrefix);
            logger.info("SMTPClient login:" + emailPrefix + "...");
            logger.info("SMTPClient ReplyString:" + client.getReplyString());

            // 设置发送者，在设置接受者之前必须要先设置发送者
            client.setSender(fromEmail);
            logger.info("设置发送者 :" + fromEmail);
            logger.info("SMTPClient ReplyString:" + client.getReplyString());

            // 设置接收者,在设置接受者必须先设置发送者，否则SMTP服务器会拒绝你的命令
            client.addRecipient(email);
            logger.info("设置接收者:" + email);
            logger.info("SMTPClient ReplyString:" + client.getReplyString());
            logger.info("SMTPClient ReplyCode：" + client.getReplyCode() + "(250表示正常)");
            if (250 == client.getReplyCode()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return false;
    }

    public void decryptLoginDto(LoginDto loginDto) {
        loginDto.setUserName(RSAUtils.decryptBase64(loginDto.getUserName()));
        loginDto.setPassword(RSAUtils.decryptBase64(loginDto.getPassword()));
        loginDto.setCaptcha(RSAUtils.decryptBase64(loginDto.getCaptcha()));
        loginDto.setNewPassword(RSAUtils.decryptBase64(loginDto.getNewPassword()));
    }
}