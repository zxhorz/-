package com.hengtiansoft.bluemorpho.workbench.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.RolePermission;
import com.hengtiansoft.bluemorpho.workbench.domain.User;
import com.hengtiansoft.bluemorpho.workbench.domain.UserRoleInProject;
import com.hengtiansoft.bluemorpho.workbench.enums.UserState;
import com.hengtiansoft.bluemorpho.workbench.repository.PermissionRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.RolePermissionRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.RoleRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRoleInProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.OperationLogger;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 13, 2018 12:54:14 PM
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleInProjectRepository userRoleInProjectRepository;
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private OperationLogger operationLogger;
	
	// 暂时简化处理，默认直接到首页，用户都为预设的Admin
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String username = token.getUsername();
		String password = String.valueOf(token.getPassword());
		User find = userRepository.findUserByName(username);
//		if (find != null && find.getPassword().equals(password)) {
//			info = new SimpleAuthenticationInfo(username, password, getName());
//			operationLogger.saveLogin(find.getId(), "UserId : " + find.getId() + " loged in the bwb system.");
//			return info;
//		} else {
//			throw new AuthenticationException("数据库不存在预设的Admin账户或其密码不为12345!");
//		}
		if (find != null) {
			if (find.getPassword().equals(password)) {
				String state = find.getState();
				if (UserState.ACTIVE.getState().equals(state)) {
					SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, password, getName());
					operationLogger.saveLogin(find.getId(), "UserId : " + find.getId() + " loged in the bwb system.");
					return info;
				} else {
					throw new AuthenticationException("User not activated!");
				}
			} else {
				throw new AuthenticationException("Password not correct!");
			}
		} else {
			throw new AuthenticationException("User not exists!");
		}
	}
	
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		List<String> roles = new ArrayList<String>();
		List<String> permissions = new ArrayList<String>();

		String userName = (String) principals.fromRealm(getName()).iterator().next();
		User find = userRepository.findUserByName(userName);

		if (find != null) {
			List<UserRoleInProject> urips = userRoleInProjectRepository.findAllByUserId(find.getId());
			for (UserRoleInProject urip : urips) {
				// 暂时简化处理，Admin的role针对所有project
				if (urip.getProjectId().equals("all")) {
					String roleId = urip.getRoleId();
					String roleName = roleRepository.findNameById(roleId);
					roles.add(roleName);
					List<RolePermission> rps = rolePermissionRepository.findAllByRoleId(roleId);
					if (rps != null && rps.size() > 0) {
						for (RolePermission rp : rps) {
							String permissionId = rp.getPermissionId();
							String permissionName = permissionRepository.findNameById(permissionId);
							permissions.add(permissionName);
						}
					}
				}
			}
		} else {
			throw new AuthorizationException("数据库不存在预设的Admin账户!");
		}
		// 为当前用户设置角色和权限
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRoles(roles);
		info.addStringPermissions(permissions);
		return info;
	}

}

