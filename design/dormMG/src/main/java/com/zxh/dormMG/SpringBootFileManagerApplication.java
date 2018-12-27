package com.zxh.dormMG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SpringBootFileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFileManagerApplication.class, args);
    }

}
