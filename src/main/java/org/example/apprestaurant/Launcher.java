package org.example.apprestaurant;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Launcher {
    
    private static ConfigurableApplicationContext applicationContext;
    
    public static void main(String[] args) {
        // Запускаем Spring Boot контекст
        // Веб-сервер отключен через application.properties (spring.main.web-application-type=none)
        // CommandLineRunner отключен (App.java без @Component)
        applicationContext = SpringApplication.run(RestaurantApplication.class, args);
        
        // Передаем контекст в JavaFX приложение
        HelloApplication.setSpringContext(applicationContext);
        
        // Запускаем JavaFX приложение
        // Application.launch не возвращает управление до закрытия приложения
        Application.launch(HelloApplication.class, args);
    }
    
    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
