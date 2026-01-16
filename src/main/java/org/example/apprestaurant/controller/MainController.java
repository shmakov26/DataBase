package org.example.apprestaurant.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.apprestaurant.Launcher;

import java.io.IOException;

public class MainController {

    @FXML
    private void openDishManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/dish-management.fxml", "Управление блюдами");
    }

    @FXML
    private void openShiftManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/shift-management.fxml", "Управление сменами");
    }

    @FXML
    private void openWaiterManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/waiter-management.fxml", "Управление официантами");
    }

    @FXML
    private void openTableManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/table-management.fxml", "Управление столиками");
    }

    @FXML
    private void openVisitorManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/visitor-management.fxml", "Управление посетителями");
    }

    @FXML
    private void openOrderManagement() throws IOException {
        loadWindow("/org/example/apprestaurant/view/order-management.fxml", "Управление заказами");
    }

    private void loadWindow(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(Launcher.getApplicationContext()::getBean);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/org/example/apprestaurant/styles/style.css").toExternalForm());
        
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
