package com.hengtiansoft.bluemorpho.workbench.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    /**    
 * @Description: TODO
 * @author gaochaodeng  
 * @date May 23, 2018   
 */
@Configuration
@AutoConfigureBefore(EmbeddedServletContainerAutoConfiguration.class)
public class ForceTomcatAutoConfiguration {

    @Bean
    TomcatEmbeddedServletContainerFactory tomcat() {
         return new TomcatEmbeddedServletContainerFactory();
    }
}
