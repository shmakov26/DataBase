package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.Dish;
import org.example.apprestaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DishController implements Initializable {

    @Autowired
    private DishService dishService;

    @FXML
    private TableView<Dish> dishTable;

    @FXML
    private TableColumn<Dish, Integer> idColumn;

    @FXML
    private TableColumn<Dish, String> nameColumn;

    @FXML
    private TableColumn<Dish, Integer> priceColumn;

    @FXML
    private TableColumn<Dish, String> ingredientsColumn;

    @FXML
    private TableColumn<Dish, Integer> weightColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextArea ingredientsField;

    @FXML
    private TextField weightField;

    @FXML
    private Label messageLabel;

    private ObservableList<Dish> dishList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dishList = FXCollections.observableArrayList();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weightGrams"));
        
        dishTable.setItems(dishList);
        refreshTable();
    }

    @FXML
    private void addDish() {
        try {
            String name = nameField.getText().trim();
            int price = Integer.parseInt(priceField.getText().trim());
            String ingredients = ingredientsField.getText().trim();
            int weight = Integer.parseInt(weightField.getText().trim());

            if (name.isEmpty() || ingredients.isEmpty()) {
                showError("Заполните все поля");
                return;
            }

            dishService.createDish(name, price, ingredients, weight);
            showSuccess("Блюдо добавлено");
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            showError("Некорректные числовые значения");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void deleteDish() {
        Dish selected = dishTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите блюдо для удаления");
            return;
        }

        try {
            dishService.deleteDish(selected.getId());
            showSuccess("Блюдо удалено");
            refreshTable();
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void refreshTable() {
        dishList.clear();
        dishList.addAll(dishService.getAllDishes());
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        ingredientsField.clear();
        weightField.clear();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-label");
        messageLabel.getStyleClass().add("error-label");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-label");
        messageLabel.getStyleClass().add("success-label");
    }
}
