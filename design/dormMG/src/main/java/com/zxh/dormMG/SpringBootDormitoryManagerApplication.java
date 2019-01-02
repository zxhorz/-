package com.zxh.dormMG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SpringBootDormitoryManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDormitoryManagerApplication.class, args);
    }

}
