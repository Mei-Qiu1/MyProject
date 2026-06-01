
package com.example.hospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 医院药品管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.example.hospital.mapper")
public class HospitalDrugManagementApplication {

    /**
     * 显式构造函数
     */
    public HospitalDrugManagementApplication() {
        super();
        // 显式构造函数，无需调用super()，Spring Boot应用启动类继承自Object
    }

    public static void main(String[] args) {
        SpringApplication.run(HospitalDrugManagementApplication.class, args);
    }
}
