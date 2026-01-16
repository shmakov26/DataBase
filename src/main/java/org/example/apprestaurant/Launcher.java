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
        
        // Проверяем, что контекст инициализирован
        if (applicationContext == null) {
            System.err.println("Ошибка: Spring контекст не инициализирован");
            System.exit(1);
        }
        
        // Проверяем, что MainController доступен
        try {
            applicationContext.getBean(org.example.apprestaurant.controller.MainController.class);
        } catch (Exception e) {
            System.err.println("Ошибка: MainController не найден в Spring контексте: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
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
