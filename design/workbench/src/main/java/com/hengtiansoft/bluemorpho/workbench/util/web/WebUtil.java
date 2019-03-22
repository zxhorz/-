package com.hengtiansoft.bluemorpho.workbench.util.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 
 * 
 * @author SC
 * 
 */
public final class WebUtil {

    private WebUtil() {

    }

    private static volatile ObjectMapper objectMapper;

    /**
     * Description: get the current request bound to current thread
     *
     * @return
     */
    public static HttpServletRequest getThreadRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * Description: get the current session bound to current thread
     *
     * @return
     */
    public static HttpSession getThreadSession() {
        return getThreadRequest().getSession(true);
    }

    /**
     * Description: determine whether current request is sent via AJAX
     *
     * @return
     */
    public static boolean isAjaxRequest() {
        return isAjaxRequest(getThreadRequest());
    }

    /**
     * Description: determine whether given request is sent via AJAX
     *
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    /**
     * Description: get the full URL for a relative path
     *
     * @param path
     * @return
     */
    public static String getFullUrlBasedOn(String path) {
        StringBuilder targetUrl = new StringBuilder();
        if (path.startsWith("/")) {
            // Do not apply context path to relative URLs.
            targetUrl.append(getThreadRequest().getContextPath());
        }
        targetUrl.append(path);
        return targetUrl.toString();
    }

    /**
     * Description: get the {@link ObjectMapper} that will be used in converter.
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static ObjectMapper getObjectMapper() { // NOSONAR
        if (objectMapper == null) {
            synchronized (WebUtil.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule("Custom Serializer", new Version(1, 0, 0, "FINAL"));
//                    module.addSerializer(new XssSantizeJsonSerializer());
//                    module.addSerializer(new PageEnumSerializer());
                    objectMapper.registerModule(module);
                }
            }
        }
        return objectMapper;
    }

}
