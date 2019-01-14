package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

import com.hengtiansoft.bluemorpho.workbench.constant.ResultCode;

/**
 * 
 * 
 * @author SC
 *
 */
public class ResultDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private T data;

    /**
     * ResultDto Constructor
     *
     */
    public ResultDto() {

    }

    /**
     * ResultDto Constructor
     *
     * @param code
     * @param message
     * @param data
     */
    public ResultDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Description: whether this is non business error
     *
     * @return
     */
    public boolean isNonBizError() {
        return ResultCode.SESSION_TIME_OUT.equals(code) || ResultCode.COMMON_ERROR.equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
