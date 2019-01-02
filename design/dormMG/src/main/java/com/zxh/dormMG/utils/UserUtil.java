package com.zxh.dormMG.utils;

import com.zxh.dormMG.Repository.RoleRepository;
import com.zxh.dormMG.Repository.UserRepository;
import com.zxh.dormMG.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
	RoleRepository roleRepository;
	
	public String getCurrentUserId() {
		String userName = String.valueOf(SecurityUtils.getSubject()
				.getSession().getAttribute("signinId"));
		User user = userRepository.findByName(userName);
		if (user != null) {
			return user.getId();
		}
		return null;
	}
	
	public String getUserName() {
		// 目前存储的是username，后续同时保存username和userid
		Subject currentUser = SecurityUtils.getSubject();
		Session s = currentUser.getSession();
		String userName = (String) s.getAttribute("signinId");
		return userName;
	}

	public String getCurrentUserRole() {
		String userId = getCurrentUserId();
		if(null==userId){
			return null;
		}
		String roleId = roleRepository.findByUserId(userId);
		return roleId;
	}
}
