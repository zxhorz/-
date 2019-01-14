package com.hengtiansoft.bluemorpho.workbench.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.hengtiansoft.bluemorpho.workbench" })
@EnableSpringDataWebSupport
@EnableJpaRepositories("com.hengtiansoft.bluemorpho.workbench.repository")
//@EnableNeo4jRepositories("com.hengtiansoft.bluemorpho.workbench.neo4j.repository")
@EntityScan("com.hengtiansoft.bluemorpho.workbench")
@ImportResource( { "classpath:applicationContext.xml", "classpath:application-mvc.xml"} )
@Configuration
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
@Import(SwaggerDocumentationConfig.class)
public class WorkBenchApplication implements CommandLineRunner {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
       MultipartConfigFactory factory = new MultipartConfigFactory();
       //  单个数据大小
       factory.setMaxFileSize("10240KB");
       /// 总上传数据大小
       factory.setMaxRequestSize("5120000KB");
       return factory.createMultipartConfig();
    }

    @Override
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

	public static void main(String[] args) throws Exception {
		// 启动py_server
//		startPyServer();
		new SpringApplication(WorkBenchApplication.class).run(args);
	}

	public static void startPyServer() {
		String toolsPath = FilePathUtil.getToolPath();
		List<String> cmd = new ArrayList<String>();
		cmd.add("python");
		cmd.add(toolsPath+"/prediction/py_server/server.py");
		ProcessBuilderUtil.startPyServer(cmd);
	}
	
    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
