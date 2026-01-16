package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.Waiter;
import org.example.apprestaurant.service.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WaiterController implements Initializable {

    @Autowired
    private WaiterService waiterService;

    @FXML
    private TableView<Waiter> waiterTable;

    @FXML
    private TableColumn<Waiter, Integer> idColumn;

    @FXML
    private TableColumn<Waiter, String> firstNameColumn;

    @FXML
    private TableColumn<Waiter, String> lastNameColumn;

    @FXML
    private TableColumn<Waiter, String> middleNameColumn;

    @FXML
    private TableColumn<Waiter, String> mailColumn;

    @FXML
    private TableColumn<Waiter, String> phoneColumn;

    @FXML
    private TableColumn<Waiter, Integer> percentColumn;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField mailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField percentField;

    @FXML
    private Label messageLabel;

    private ObservableList<Waiter> waiterList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        waiterList = FXCollections.observableArrayList();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        mailColumn.setCellValueFactory(new PropertyValueFactory<>("mail"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        
        waiterTable.setItems(waiterList);
        refreshTable();
    }

    @FXML
    private void addWaiter() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String mail = mailField.getText().trim();
            String phone = phoneField.getText().trim();
            int percent = Integer.parseInt(percentField.getText().trim());

            if (firstName.isEmpty() || lastName.isEmpty() || mail.isEmpty() || phone.isEmpty()) {
                showError("Заполните обязательные поля");
                return;
            }

            waiterService.createWaiter(firstName, lastName, middleName, mail, phone, percent);
            showSuccess("Официант добавлен");
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            showError("Некорректное значение процента");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void refreshTable() {
        waiterList.clear();
        waiterList.addAll(waiterService.getWaiters());
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        middleNameField.clear();
        mailField.clear();
        phoneField.clear();
        percentField.clear();
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
