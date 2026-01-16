package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.Shift;
import org.example.apprestaurant.entity.Waiter;
import org.example.apprestaurant.service.ShiftService;
import org.example.apprestaurant.service.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ShiftController implements Initializable {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private WaiterService waiterService;

    @FXML
    private TableView<Shift> shiftTable;

    @FXML
    private TableColumn<Shift, Integer> idColumn;

    @FXML
    private TableColumn<Shift, String> waiterColumn;

    @FXML
    private TableColumn<Shift, String> startColumn;

    @FXML
    private TableColumn<Shift, String> endColumn;

    @FXML
    private TableColumn<Shift, Integer> tipsColumn;

    @FXML
    private ComboBox<Waiter> waiterCombo;

    @FXML
    private ComboBox<Integer> shiftCombo;

    @FXML
    private RadioButton openShiftsRadio;

    @FXML
    private RadioButton waiterShiftsRadio;

    @FXML
    private Label messageLabel;

    private ObservableList<Shift> shiftList;
    private ObservableList<Waiter> waiterList;
    private ObservableList<Integer> shiftIdList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shiftList = FXCollections.observableArrayList();
        waiterList = FXCollections.observableArrayList();
        shiftIdList = FXCollections.observableArrayList();
        
        // ID колонка скрыта
        idColumn.setVisible(false);
        waiterColumn.setCellValueFactory(cellData -> {
            Waiter waiter = cellData.getValue().getWaiter();
            return new javafx.beans.property.SimpleStringProperty(
                waiter != null ? waiter.getLastName() + " " + waiter.getFirstName() : ""
            );
        });
        startColumn.setCellValueFactory(cellData -> {
            var start = cellData.getValue().getShiftStart();
            return new javafx.beans.property.SimpleStringProperty(
                start != null ? start.toString() : ""
            );
        });
        endColumn.setCellValueFactory(cellData -> {
            var end = cellData.getValue().getShiftEnd();
            return new javafx.beans.property.SimpleStringProperty(
                end != null ? end.toString() : ""
            );
        });
        tipsColumn.setCellValueFactory(new PropertyValueFactory<>("tipsCollected"));
        
        shiftTable.setItems(shiftList);
        
        waiterCombo.setItems(waiterList);
        // Настройка ComboBox для отображения только имени официанта
        waiterCombo.setConverter(new javafx.util.StringConverter<Waiter>() {
            @Override
            public String toString(Waiter waiter) {
                return waiter != null ? waiter.getLastName() + " " + waiter.getFirstName() + " (ID: " + waiter.getId() + ")" : "";
            }

            @Override
            public Waiter fromString(String string) {
                return waiterList.stream()
                        .filter(w -> (w.getLastName() + " " + w.getFirstName() + " (ID: " + w.getId() + ")").equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        
        shiftCombo.setItems(shiftIdList);
        
        ToggleGroup group = new ToggleGroup();
        openShiftsRadio.setToggleGroup(group);
        waiterShiftsRadio.setToggleGroup(group);
        openShiftsRadio.setSelected(true);
        
        refreshWaiters();
        refreshOpenShifts();
    }

    @FXML
    private void openShift() {
        Waiter selected = waiterCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите официанта");
            return;
        }

        try {
            shiftService.openShift(selected.getId());
            showSuccess("Смена открыта");
            refreshOpenShifts();
            refreshWaiters();
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void closeShift() {
        Integer selected = shiftCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите смену");
            return;
        }

        try {
            shiftService.closeShift(selected);
            showSuccess("Смена закрыта");
            refreshOpenShifts();
            if (waiterShiftsRadio.isSelected()) {
                refreshWaiterShifts();
            }
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onRadioChanged() {
        if (openShiftsRadio.isSelected()) {
            refreshOpenShifts();
        } else {
            refreshWaiterShifts();
        }
    }

    @FXML
    private void refreshWaiterShifts() {
        Waiter selected = waiterCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите официанта");
            return;
        }

        try {
            shiftList.clear();
            List<Shift> shifts = shiftService.getShiftsByWaiter(selected.getId());
            // Копируем данные в ObservableList, чтобы избежать проблем с ленивой загрузкой
            shiftList.addAll(shifts);
        } catch (Exception e) {
            showError("Ошибка загрузки смен: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshOpenShifts() {
        try {
            shiftList.clear();
            List<Shift> shifts = shiftService.getOpenShifts();
            // Копируем данные в ObservableList, чтобы избежать проблем с ленивой загрузкой
            shiftList.addAll(shifts);
            
            shiftIdList.clear();
            shiftList.forEach(s -> shiftIdList.add(s.getId()));
        } catch (Exception e) {
            showError("Ошибка загрузки смен: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshWaiters() {
        waiterList.clear();
        waiterList.addAll(waiterService.getWaiters());
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
