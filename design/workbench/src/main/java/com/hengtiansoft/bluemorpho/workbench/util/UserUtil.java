package com.hengtiansoft.bluemorpho.workbench.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.domain.User;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRoleInProjectRepository;

/**
 * @Description: User的一些操作
 * @author gaochaodeng
 * @date May 21, 2018
 */
@Component
public class UserUtil {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleInProjectRepository userRoleInProjectRepository;
	
	public String getCurrentUserId() {
		String userName = String.valueOf(SecurityUtils.getSubject()
				.getSession().getAttribute("signinId"));
		User user = userRepository.findUserByName(userName);
		if (user != null) {
			return user.getId();
		}
		return null;
	}
	
	public static String getUserName() {
		// 目前存储的是username，后续同时保存username和userid
		Subject currentUser = SecurityUtils.getSubject();
		Session s = currentUser.getSession();
		String userName = (String) s.getAttribute("signinId");
		return userName;
	}

	public String getCurrentUserRole(String projectId) {
		String userId = getCurrentUserId();
		if(null==userId){
			return null;
		}
		String roleId = userRoleInProjectRepository.findByUserIdAndProjectId(userId,projectId);
		if(null==roleId || roleId.isEmpty()){
			roleId = userRoleInProjectRepository.findByUserIdAndProjectId(userId,"all");	
		}
		return roleId;
	}
}
