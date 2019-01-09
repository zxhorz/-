package com.zxh.dormMG.Service;

import com.zxh.dormMG.Repository.RoleRepository;
import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.domain.Authentication;
import com.zxh.dormMG.domain.Permission;
import com.zxh.dormMG.domain.Role;
import com.zxh.dormMG.domain.User;
import com.zxh.dormMG.dto.RegisterDto;
import com.zxh.dormMG.dto.ResultDto;
import com.zxh.dormMG.enums.UserState;
import com.zxh.dormMG.utils.PasswordUtil;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@Transactional
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    //添加用户
    public User addUser(Map<String, Object> map) {
        User user = new User();
        user.setUsername(map.get("username").toString());
        user.setPassword(map.get("password").toString());
        userRepository.save(user);
        return user;
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
        List<Permission> permissions = new ArrayList<Permission>();
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


    public static boolean sendMail(String to, String code) {

        try {
            Properties props = new Properties();
            props.put("username", "huyuyang6688@163.com");
            props.put("password", "123456");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "smtp.163.com");
            props.put("mail.smtp.port", "25");

            Session mailSession = Session.getDefaultInstance(props);

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("huyuyang6688@163.com"));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("激活邮件");
            msg.setContent("<h1>此邮件为官方激活邮件！请点击下面链接完成激活操作！</h1><h3><a href='http://localhost:8080/SendMail/servlet/ActiveServlet?code=" + code + "'>http://localhost:8080/SendMail/servlet/ActiveServlet</a></h3>", "text/html;charset=UTF-8");
            msg.saveChanges();

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(props.getProperty("mail.smtp.host"), props
                    .getProperty("username"), props.getProperty("password"));
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
        return true;
    }


    public static boolean checkEmail(String email) {
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            logger.error("邮箱（" + email + "）校验未通过，格式不对!");
            return false;
        }
        String host = "";
        String hostName = email.split("@")[1];
        Record[] result = null;
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
            for (int i = 0; i < result.length; i++) {
                host = result[i].getAdditionalName().toString();
                logger.info("SMTPClient try connect to host:" + host);

                // 尝试Socket连接到SMTP服务器
                client.connect(host);
                // 查看响应码是否正常
                // 所有以2开头的响应码都是正常的响应
                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
                    // 断开socket连接
                    client.disconnect();
                    continue;
                } else {
                    logger.info("找到MX记录:" + hostName);
                    logger.info("建立链接成功：" + hostName);
                    break;
                }
            }
            logger.info("SMTPClient ReplyString:" + client.getReplyString());
            String emailSuffix = "hengtiansoft.com";
            String emailPrefix = "xihaozhou";
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
            }
        }
        return false;
    }

    public ResultDto<String> register(String userName) {
        ResultDto<String> resultDto = new ResultDto<String>();
        boolean exists = checkEmail(userName);
        if (exists) {
            resultDto.setCode("E");
            resultDto.setData("Invalid Email.");
            return resultDto;
        } else {
            if (userRepository.findUserByName(userName) != null) {
                resultDto.setCode("E");
                resultDto.setData("The Email has been registered.");
                return resultDto;
            } else {
                resultDto.setData(null);
                // 4位激活码
                String activationCode = PasswordUtil.generateRandomString(4);
                // 6位密码
                String password = PasswordUtil.generateRandomString(6);
                User user = new User();
                user.setUsername(userName);
                user.setPassword(PasswordUtil.MD5(password));
                user.setActivationCode(activationCode);
                user.setState(UserState.NON_ACTIVE.getState());
                userRepository.save(user);
                // 发送密码和激活码到注册邮箱
                try {
                    sendPasswordAndActivationCode(activationCode, password, userName);
                } catch (Exception e) {
                    resultDto.setCode("E");
                    resultDto.setData("Send activation email exception!");
                    return resultDto;
                }
                resultDto.setCode("S");
                resultDto.setData("The user was successfully registered, please activate your account in your email!");
                return resultDto;
            }
        }
    }

    private void sendPasswordAndActivationCode(String activationCode, String password, String toEmail) throws MessagingException {
        Properties props = new Properties();
        props.put("username", "huangchendong111@163.com");
        props.put("password", "13336100573");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");

        Authentication authentication = new Authentication("huangchendong111@163.com", "13336100573");
        Session mailSession = Session.getDefaultInstance(props, authentication);

        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress("huangchendong111@163.com"));
        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("DeepMorpho产品激活邮件");
        msg.setContent("<h1>此邮件为DeepMorpho产品官方激活邮件，您注册的账号为："
                + toEmail
                + "， 密码为："
                + password
                + "，请点击下面链接完成激活操作！</h1><h3><a href='http://localhost:8080/activeAccount?activationCode="
                + activationCode
                + "&account="
                + toEmail
                + "'>http://localhost:8080/activeAccount</a></h3>", "text/html;charset=UTF-8");
        msg.saveChanges();

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(props.getProperty("mail.smtp.host"), props
                .getProperty("username"), props.getProperty("password"));
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    public ModelAndView activeAccount(String activationCode, String account) {
        User user = userRepository.findUserByActivationCode(activationCode);
        if (user != null) {
            user.setState(UserState.ACTIVE.getState());
            userRepository.save(user);
        }
        return new ModelAndView("redirect:/static/login-module/login.html");
    }


}