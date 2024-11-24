package ru.fourbarman.planner.micro.plannertodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories(basePackages =
        "ru.fourbarman.planner.micro.plannertodo.repository")
@EntityScan("ru.fourbarman.planner.micro.plannerentity.entity")
@EnableFeignClients
@RefreshScope
public class PlannerTodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlannerTodoApplication.class, args);
    }

}
