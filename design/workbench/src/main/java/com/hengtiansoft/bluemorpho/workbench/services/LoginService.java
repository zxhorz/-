//package com.hengtiansoft.bluemorpho.workbench.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.hengtiansoft.bluemorpho.workbench.domain.User;
//import com.hengtiansoft.bluemorpho.workbench.dto.RegisterDto;
//import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
//import com.hengtiansoft.bluemorpho.workbench.util.PasswordUtil;
//@Service
//public class LoginService {
//    @Autowired
//    UserRepository userRepository;
////    public boolean sendEmail(String emailName, String password, String email, String title, String content, String smtp) {
////        }
////    }
//
//public static boolean checkEmail(String email) {
//        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
//            logger.error("邮箱（" + email + "）校验未通过，格式不对!");
//            return false;
//        }
//        String host = "";
//        String hostName = email.split("@")[1];
//        Record[] result = null;
//        SMTPClient client = new SMTPClient();
//        try {
//            // 查找DNS缓存服务器上为MX类型的缓存域名信息
//            Lookup lookup = new Lookup(hostName, Type.MX);
//            lookup.run();
//            if (lookup.getResult() != Lookup.SUCCESSFUL) {// 查找失败
//                logger.error("邮箱（" + email + "）校验未通过，未找到对应的MX记录!");
//                return false;
//            } else {// 查找成功
//                result = lookup.getAnswers();
//            }
//            // 尝试和SMTP邮箱服务器建立Socket连接
//            for (int i = 0; i < result.length; i++) {
//                host = result[i].getAdditionalName().toString();
//                logger.info("SMTPClient try connect to host:" + host);
//
//                // 尝试Socket连接到SMTP服务器
//                client.connect(host);
//                // 查看响应码是否正常
//                // 所有以2开头的响应码都是正常的响应
//                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
//                    // 断开socket连接
//                    client.disconnect();
//                    continue;
//                } else {
//                    logger.info("找到MX记录:" + hostName);
//                    logger.info("建立链接成功：" + hostName);
//                    break;
//                }
//            }
//            logger.info("SMTPClient ReplyString:" + client.getReplyString());
//            String emailSuffix = "hengtiansoft.com";
//            String emailPrefix = "xihaozhou";
//            String fromEmail = emailPrefix + "@" + emailSuffix;
//            // 尝试和SMTP服务器建立连接,发送一条消息给SMTP服务器
//            client.login(emailPrefix);
//            logger.info("SMTPClient login:" + emailPrefix + "...");
//            logger.info("SMTPClient ReplyString:" + client.getReplyString());
//
//            // 设置发送者，在设置接受者之前必须要先设置发送者
//            client.setSender(fromEmail);
//            logger.info("设置发送者 :" + fromEmail);
//            logger.info("SMTPClient ReplyString:" + client.getReplyString());
//
//            // 设置接收者,在设置接受者必须先设置发送者，否则SMTP服务器会拒绝你的命令
//            client.addRecipient(email);
//            logger.info("设置接收者:" + email);
//            logger.info("SMTPClient ReplyString:" + client.getReplyString());
//            logger.info("SMTPClient ReplyCode：" + client.getReplyCode() + "(250表示正常)");
//            if (250 == client.getReplyCode()) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                client.disconnect();
//            } catch (IOException e) {
//            }
//        }
//        return false;
//    }
//    
//    public String register(RegisterDto registerDto) {
//        // TODO Auto-generated method stub
//        String userName = registerDto.getUserName();
//        //验证邮箱有效性
//        if(!checkEmail(userName)){
//            throw new RuntimeException("Invalid email");
//        }
//        //验证邮箱是否被注册
//        if(userRepository.findUserByName(userName) == null) {
//            userRepository.save(new User(userName,PasswordUtil.generatePassword()));
//        } else {
//            throw new RuntimeException("Email has been registered");
//        }
//        //发送密码至邮箱
//        
//        return "Mail of password has been sent, please note to check";
//    }
//}
