package org.example.apprestaurant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class HelloApplication extends Application {
    
    private static ConfigurableApplicationContext springContext;
    
    public static void setSpringContext(ConfigurableApplicationContext context) {
        springContext = context;
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/apprestaurant/view/main-view.fxml"));
        
        // Если есть Spring контекст, используем его для загрузки контроллера
        if (springContext != null) {
            fxmlLoader.setControllerFactory(springContext::getBean);
        }
        
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("/org/example/apprestaurant/styles/style.css").toExternalForm());
        stage.setTitle("Система управления рестораном");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        // Закрываем Spring контекст при закрытии приложения
        if (springContext != null) {
            springContext.close();
        }
    }
}
