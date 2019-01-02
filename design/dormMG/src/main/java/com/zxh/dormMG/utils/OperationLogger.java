package com.zxh.dormMG.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zxh.dormMG.Repository.OperationLogRepository;
import com.zxh.dormMG.Repository.RoleRepository;
import com.zxh.dormMG.domain.OperationLog;
import com.zxh.dormMG.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationLogger {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserUtil userUtil;

    /**
     * 记录project-admin更改他人权限的操作
     */
    public void savePrivilegeChange(String manipulatorUserId,
                                    String manipulatedUserId, String roleId, String detail) {
        OperationLog op = new OperationLog(manipulatorUserId, manipulatedUserId,
                roleId, OperationType.CHANGE_PRIVILEGE.toString(),
                detail, getCurrentTime());
        operationLogRepository.save(op);
    }

    /**
     * 记录登录操作
     */
    public void saveLogin(String manipulatorUserId, String detail) {
        OperationLog op = new OperationLog(manipulatorUserId, OperationType.LOGIN.toString(), detail, getCurrentTime());
        operationLogRepository.save(op);
    }

    /**
     * 记录登出操作
     */
    public void saveLogout(String manipulatorUserId, String detail) {
        OperationLog op = new OperationLog(manipulatorUserId, OperationType.LOGOUT.toString(), detail, getCurrentTime());
        operationLogRepository.save(op);
    }

    private String getCurrentTime() {
        return getCurrentTime(new Date());
    }

    private String getCurrentTime(Date startTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(startTime);
    }
}

