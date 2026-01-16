package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.Visitor;
import org.example.apprestaurant.service.ApplicationStateService;
import org.example.apprestaurant.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class VisitorController implements Initializable {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private ApplicationStateService stateService;

    @FXML
    private TableView<Visitor> visitorTable;

    @FXML
    private TableColumn<Visitor, Integer> idColumn;

    @FXML
    private TableColumn<Visitor, Integer> balanceColumn;

    @FXML
    private TableColumn<Visitor, String> stateColumn;

    @FXML
    private TableColumn<Visitor, Integer> tableColumn;

    @FXML
    private TextField balanceField;

    @FXML
    private ComboBox<Integer> visitorCombo;

    @FXML
    private Label messageLabel;

    @FXML
    private Label currentVisitorLabel;

    private ObservableList<Visitor> visitorList;
    private ObservableList<Integer> visitorIdList;
    private Integer currentVisitorId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        visitorList = FXCollections.observableArrayList();
        visitorIdList = FXCollections.observableArrayList();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("walletBalance"));
        stateColumn.setCellValueFactory(cellData -> {
            var state = cellData.getValue().getState();
            return new javafx.beans.property.SimpleStringProperty(
                state != null ? state.toString() : ""
            );
        });
        tableColumn.setCellValueFactory(cellData -> {
            var table = cellData.getValue().getTable();
            return new javafx.beans.property.SimpleIntegerProperty(
                table != null ? table.getNumber() : 0
            ).asObject();
        });
        
        visitorTable.setItems(visitorList);
        visitorCombo.setItems(visitorIdList);
        
        refreshTable();
        currentVisitorId = stateService.getCurrentVisitorId();
        updateCurrentVisitorLabel();
    }

    @FXML
    private void createVisitor() {
        try {
            int balance = Integer.parseInt(balanceField.getText().trim());
            Visitor visitor = visitorService.createVisitor(balance);
            showSuccess("Посетитель создан (ID: " + visitor.getId() + ")");
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            showError("Некорректное значение баланса");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void selectCurrentVisitor() {
        Integer selected = visitorCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите посетителя");
            return;
        }
        stateService.setCurrentVisitorId(selected);
        currentVisitorId = selected;
        updateCurrentVisitorLabel();
        showSuccess("Текущий посетитель: ID " + selected);
    }

    @FXML
    private void refreshTable() {
        visitorList.clear();
        visitorList.addAll(visitorService.showVisitors());
        
        visitorIdList.clear();
        visitorList.forEach(v -> visitorIdList.add(v.getId()));
    }

    private void clearFields() {
        balanceField.clear();
    }

    private void updateCurrentVisitorLabel() {
        Integer visitorId = stateService.getCurrentVisitorId();
        if (visitorId != null) {
            currentVisitorLabel.setText("Текущий посетитель: ID " + visitorId);
        } else {
            currentVisitorLabel.setText("Текущий посетитель: не выбран");
        }
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
