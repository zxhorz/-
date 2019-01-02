package com.zxh.dormMG.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 15, 2018 11:33:08 AM
 */
@Entity
@Table(name = "operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "manipulator_id")
    private String manipulatorId;

    @Column(name = "manipulated_id")
    private String manipulatedId;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "operation_detail")
    private String operationDetail;

    @Column(name = "operation_time")
    private String operationTime;

    public OperationLog() {
        super();
    }

    public OperationLog(String manipulatorId, String operationType, String operationDetail, String operationTime) {
        super();
        this.manipulatorId = manipulatorId;
        this.operationType = operationType;
        this.operationDetail = operationDetail;
        this.operationTime = operationTime;
    }

    public OperationLog(String manipulatorId, String roleId,
                        String operationType, String operationDetail, String operationTime) {
        super();
        this.manipulatorId = manipulatorId;
        this.roleId = roleId;
        this.operationType = operationType;
        this.operationDetail = operationDetail;
        this.operationTime = operationTime;
    }

    public OperationLog(String manipulatorId, String manipulatedId,
                        String roleId, String operationType,
                        String operationDetail, String operationTime) {
        super();
        this.manipulatorId = manipulatorId;
        this.manipulatedId = manipulatedId;
        this.roleId = roleId;
        this.operationType = operationType;
        this.operationDetail = operationDetail;
        this.operationTime = operationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManipulatorId() {
        return manipulatorId;
    }

    public void setManipulatorId(String manipulatorId) {
        this.manipulatorId = manipulatorId;
    }

    public String getManipulatedId() {
        return manipulatedId;
    }

    public void setManipulatedId(String manipulatedId) {
        this.manipulatedId = manipulatedId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationDetail() {
        return operationDetail;
    }

    public void setOperationDetail(String operationDetail) {
        this.operationDetail = operationDetail;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }


}
