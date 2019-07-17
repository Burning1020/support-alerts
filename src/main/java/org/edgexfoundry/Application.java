package org.edgexfoundry;

import org.edgexfoundry.service.impl.ZeroMQEventSubscriberImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableMongoAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"org.edgexfoundry", "org.springframework.cloud.client"})

public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        String welcomeMsg = ctx.getEnvironment().getProperty("app.open.msg");
        ZeroMQEventSubscriberImpl sub = ctx.getBean(ZeroMQEventSubscriberImpl.class);
        sub.receive();
        System.out.println(welcomeMsg);
    }
}
