package com.zxh.dormMG.Security;

import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.Service.LoginService;
import com.zxh.dormMG.domain.Permission;
import com.zxh.dormMG.domain.Role;
import com.zxh.dormMG.domain.User;
import com.zxh.dormMG.utils.OperationLogger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

//实现AuthorizingRealm接口用户用户认证
public class MyShiroRealm extends AuthorizingRealm {

    //用于用户查询
    @Autowired
    private OperationLogger operationLogger;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;
    //角色权限和对应权限添加
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String name = (String) principalCollection.getPrimaryPrincipal();
        //查询用户名称
        User user = loginService.findByName(name);
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Role role : user.getRoles()) {
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRoleName());
            for (Permission permission : role.getPermissions()) {
                //添加权限
                simpleAuthorizationInfo.addStringPermission(permission.getPermission());
            }
        }
        return simpleAuthorizationInfo;
    }

    //用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String username = token.getUsername();
        String password = String.valueOf(token.getPassword());
        User find = userRepository.findByName(username);
//		if (find != null && find.getPassword().equals(password)) {
//			info = new SimpleAuthenticationInfo(username, password, getName());
//			operationLogger.saveLogin(find.getId(), "UserId : " + find.getId() + " loged in the bwb system.");
//			return info;
//		} else {
//			throw new AuthenticationException("数据库不存在预设的Admin账户或其密码不为12345!");
//		}
        if (find != null) {
            if (find.getPassword().equals(password)) {
                SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, password, getName());
                operationLogger.saveLogin(find.getName(), "UserId : " + find.getId() + " loged in the bwb system.");
                return info;
            } else {
                throw new AuthenticationException("Password not correct!");
            }
        } else {
            throw new AuthenticationException("User not exists!");
        }
    }
}