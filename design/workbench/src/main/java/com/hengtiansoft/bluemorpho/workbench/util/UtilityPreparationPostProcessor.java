package com.hengtiansoft.bluemorpho.workbench.util;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 * @author SC
 *
 */
@Component
public class UtilityPreparationPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilityPreparationPostProcessor.class);

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @Autowired
    @Qualifier("appConfig")
    private MessageSource appConfig;

    @PostConstruct
    public void postProcessAfterInitialization() throws BeansException {
        LOGGER.info("postProcessAfterInitialization() invoked");

        MessageUtil.setMessageSource(messageSource);
        AppConfigUtil.setMessageSource(appConfig);
    }

}
