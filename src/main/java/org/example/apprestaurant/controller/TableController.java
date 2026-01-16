package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.TableEntity;
import org.example.apprestaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class TableController implements Initializable {

    @Autowired
    private TableService tableService;

    @FXML
    private TableView<TableEntity> tableTable;

    @FXML
    private TableColumn<TableEntity, Integer> idColumn;

    @FXML
    private TableColumn<TableEntity, Integer> numberColumn;

    @FXML
    private TableColumn<TableEntity, Integer> seatsColumn;

    @FXML
    private TableColumn<TableEntity, String> statusColumn;

    @FXML
    private TextField numberField;

    @FXML
    private TextField seatsField;

    @FXML
    private RadioButton allTablesRadio;

    @FXML
    private RadioButton freeTablesRadio;

    @FXML
    private Label messageLabel;

    private ObservableList<TableEntity> tableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableList = FXCollections.observableArrayList();
        
        // ID колонка скрыта
        idColumn.setVisible(false);
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        statusColumn.setCellValueFactory(cellData -> {
            var status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(
                status != null ? status.toString() : ""
            );
        });
        
        tableTable.setItems(tableList);
        
        ToggleGroup group = new ToggleGroup();
        allTablesRadio.setToggleGroup(group);
        freeTablesRadio.setToggleGroup(group);
        allTablesRadio.setSelected(true);
        
        refreshAllTables();
    }

    @FXML
    private void addTable() {
        try {
            int number = Integer.parseInt(numberField.getText().trim());
            int seats = Integer.parseInt(seatsField.getText().trim());

            tableService.createTable(number, seats);
            showSuccess("Столик создан");
            clearFields();
            refreshAllTables();
        } catch (NumberFormatException e) {
            showError("Некорректные числовые значения");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void deleteTable() {
        TableEntity selected = tableTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите столик для удаления");
            return;
        }

        try {
            tableService.deleteTable(selected.getId());
            showSuccess("Столик удалён");
            refreshAllTables();
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onRadioChanged() {
        if (allTablesRadio.isSelected()) {
            refreshAllTables();
        } else {
            refreshFreeTables();
        }
    }

    private void refreshAllTables() {
        tableList.clear();
        tableList.addAll(tableService.getAllTables());
    }

    private void refreshFreeTables() {
        tableList.clear();
        tableList.addAll(tableService.getFreeTables());
    }

    private void clearFields() {
        numberField.clear();
        seatsField.clear();
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
