package com.hengtiansoft.bluemorpho.workbench.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.validation.BindingResult;

import com.hengtiansoft.bluemorpho.workbench.constant.ResultCode;
import com.hengtiansoft.bluemorpho.workbench.enums.EErrorCode;
import com.hengtiansoft.bluemorpho.workbench.exception.BaseException;
import com.hengtiansoft.bluemorpho.workbench.exception.DisplayableError;
import com.hengtiansoft.bluemorpho.workbench.util.AppConfigUtil;
import com.hengtiansoft.bluemorpho.workbench.util.MessageUtil;

/**
 * 
 * 
 * @author SC
 *
 */
public final class ResultDtoFactory {

    private ResultDtoFactory() {
    };

    public static <T> ResultDto<T> toAck(String msg) {
        return toAck(msg, null);
    }

    public static <T> ResultDto<T> toAck(String msg, T data) {
        ResultDto<T> dto = new ResultDto<T>();
        dto.setCode(ResultCode.ACK);
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

    public static ResultDto<String> toRedirect(String url) {
        ResultDto<String> dto = new ResultDto<String>();
        dto.setCode(ResultCode.REDIRECT);
        dto.setData(url);
        return dto;
    }

    /**
     * Description: 在controller层直接返回错误消息，避免在controller中用该方法catch异常做处理
     * 
     * @param msg
     * @return
     */
    public static <T> ResultDto<T> toNack(String msg) {
        return toNack(msg, null);
    }

    /**
     * Description: 在controller层直接返回错误消息，避免在controller中用该方法catch异常做处理
     * 
     * @param error
     * @return
     */
    public static <T> ResultDto<T> toNack(DisplayableError error) {
        String msg = "";
        if (error != null && StringUtils.isNotBlank(error.getErrorCode())) {
            msg = MessageUtil.getMessage(error.getDisplayMsg(), error.getArgs());
        }
        return toNack(msg, null);
    }

    /**
     * Description: 在controller层直接返回错误消息，避免在controller中用该方法catch异常做处理
     * 
     * @param msg
     * @param data
     * @return
     */
    public static <T> ResultDto<T> toNack(String msg, T data) {
        ResultDto<T> dto = new ResultDto<T>();
        dto.setCode(ResultCode.NACK);
        dto.setMessage(msg);
        dto.setData(data);
        return dto;
    }

    public static ResultDto<BindingResult> toValidationError(String msg, BindingResult br) {
        ResultDto<BindingResult> dto = new ResultDto<BindingResult>();
        dto.setCode(ResultCode.VALIDATION_ERROR);
        dto.setMessage(msg);
        dto.setData(br);
        return dto;
    }

    private static ResultDto<String> toCommonError(String code, String msg, String details) {
        ResultDto<String> dto = new ResultDto<String>();
        dto.setCode(ResultCode.COMMON_ERROR);
        StringBuilder text = new StringBuilder();
        if (StringUtils.isBlank(msg)) {
            text.append(MessageUtil.getMessage(EErrorCode.COMM_INTERNAL_ERROR.getDisplayMsg())).append("[")
                    .append("时间：").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())).append("]");
        } else {
            text.append(msg);
        }
        if (StringUtils.isNotBlank(code)) {
            text.append("(").append(code).append(")");
        }
        dto.setMessage(text.toString());
        if (!AppConfigUtil.isProdEnv()) {
            dto.setData(details);
        }
        return dto;
    }

    /**
     * Description: 异常的stacktrace和message将在非生产环境中显示出来
     * 
     * @param e
     * @return
     */
    public static ResultDto<String> toCommonError(BaseException e) {
        String msg = MessageUtil.getMessage(e.getError().getDisplayMsg());
        return toCommonError(e.getError().getErrorCode(), msg, ExceptionUtils.getStackTrace(e));
    }

    /**
     * Description: 异常的stacktrace和message将在非生产环境中显示出来
     * 
     * @param e
     * @return
     */
    public static ResultDto<String> toCommonError(Exception e) {
        return toCommonError(null, null, ExceptionUtils.getStackTrace(e));
    }

    /**
     * Description: 传入的msg将显示出来
     * 
     * @param msg
     * @return
     */
    public static ResultDto<String> toCommonError(String msg) {
        return toCommonError(null, msg, null);
    }

    public static ResultDto<String> toBizError(String msg, Exception e) {
        ResultDto<String> dto = new ResultDto<String>();
        dto.setCode(ResultCode.BUSINESS_ERROR);
        dto.setMessage(msg == null ? e.getMessage() : msg);
        if (!AppConfigUtil.isProdEnv()) {
            dto.setData(ExceptionUtils.getStackTrace(e));
        }
        return dto;
    }
}
