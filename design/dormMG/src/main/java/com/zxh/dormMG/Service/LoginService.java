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
import com.zxh.dormMG.utils.FilePathUtil;
import com.zxh.dormMG.utils.PasswordUtil;
import com.zxh.dormMG.utils.PortUtil;
import com.zxh.dormMG.utils.RSAUtils;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@Transactional
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class);
    private static final String LOCALPORT = ":8888";
    private static final String EMAIL_SUFFIX = "@stu.zucc.edu.cn";
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
        return sessionCode.equalsIgnoreCase(captcha);
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
            resultDto.setData("用户不存在");
            return resultDto;
        } else {
            resultDto.setData(null);
            // 6位激活码
            String activationCode = PasswordUtil.generateRandomString(6);
            // 发送激活码到注册邮箱
            try {
                sendChangePassword(activationCode, userName + EMAIL_SUFFIX);
            } catch (Exception e) {
                resultDto.setCode("E");
                resultDto.setData("邮件发送失败");
                return resultDto;
            }
            User user = userRepository.findUserByName(userName);
            user.setActivationCode(PasswordUtil.MD5(activationCode));
            user.setTime("0");
            userRepository.save(user);
            resultDto.setCode("S");
            resultDto.setData("邮件发送成功，请在30分钟内输入验证码");
            return resultDto;
        }
    }

    public ResultDto<String> register(String userName) {
        ResultDto<String> resultDto = new ResultDto<>();
        User user = userRepository.findUserByName(userName);
        boolean exists = (user != null);
        if (!checkEmail(userName + EMAIL_SUFFIX)) {
            resultDto.setCode("E");
            resultDto.setData("非法用户");
            return resultDto;
        }
        if (exists && user.getState().equals(UserState.ACTIVE.getState())) {
            resultDto.setCode("E");
            resultDto.setData("用户已存在");
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
                sendPasswordAndActivationCode(activationCode, userName + EMAIL_SUFFIX);
            } catch (Exception e) {
                resultDto.setCode("E");
                resultDto.setData("邮件发送失败");
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

    public ResultDto<String> changePassword(LoginDto loginDto) {
        String userName = loginDto.getUserName();
        String password = loginDto.getPassword();
        String newPassword = loginDto.getNewPassword();
        User user = userRepository.findUserByName(userName);
        if (user != null) {
            if (PasswordUtil.MD5(password).equals(user.getPassword())) {
                user.setPassword(PasswordUtil.MD5(newPassword));
                user.setState(UserState.ACTIVE.getState());
                user.setActivationCode(null);
                user.setTime(null);
                userRepository.save(user);
                return ResultDtoFactory.toAck("S", "密码修改成功");
            } else
                return ResultDtoFactory.toAck("F", "旧密码错误");
        } else {
            return ResultDtoFactory.toAck("F", "密码修改失败");
        }
    }

    public ResultDto<String> checkForgotActivationCode(String email, String activationCode) {
        // TODO Auto-generated method stub
        User user = userRepository.findUserByName(email);
        if (user == null)
            return ResultDtoFactory.toAck("F", "无此用户");
        String time = user.getTime();
        if (time == null)
            return ResultDtoFactory.toAck("F", "验证码错误");
        if (user.getState().equals(UserState.ACTIVE.getState()) && Integer.valueOf(time) > 30) {
            user.setActivationCode(null);
            user.setTime(null);
            userRepository.save(user);
            return ResultDtoFactory.toAck("F", "验证码过期");
        }
        if (user.getActivationCode().equals(PasswordUtil.MD5(activationCode))) {
            user.setActivationCode(null);
            user.setTime(null);
            return ResultDtoFactory.toAck("S");
        } else
            return ResultDtoFactory.toAck("F", "验证码错误");
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
        msg.setSubject("Dorm Manager Activation Email");
        String content = "<h1>This is an official activation email from Dorm Manager,please dont't reply. " +
                "<br/> Please click on the link below to complete activation and set your own password!</h1><h3><a href='http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/login/activeAccount?activationCode=" +
                activationCode +
                "&account=" +
                toEmail +
                "'>http://" +
                portUtil.getWbServerIp() + LOCALPORT +
                "/activeAccount</a></h3>";
        //        "<h1>This is an official activation email from Dorm Manager,please dont't reply. "
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
        msg.setFrom(new InternetAddress("DormManager<" + fromUserName + ">"));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("Dorm Manager Activation Email");
        String content = "<h1>您的邮箱绑定的用户正在找回密码，请不要回复本邮件，如非本人操作，请勿理会。</h1>" +
                "<h3>验证码为 " + activationCode + " </h3>";
        //        "<h1>This is an official activation email from Dorm Manager,please dont't reply."
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