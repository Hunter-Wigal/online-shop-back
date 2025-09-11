package com.shop.online_shop;

import com.shop.online_shop.controllers.AuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class OnlineShopApplication  implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;
    public static void main(String[] args)
    {
      SpringApplication.run(OnlineShopApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        applicationContext.getBean(AuthController.class).addTempUser("admin@gmail.com", "admin", "ADMIN");
        applicationContext.getBean(AuthController.class).addTempUser("test@gmail.com", "password", "USER");
    }
}
