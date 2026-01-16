package org.example.apprestaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.apprestaurant.entity.Dish;
import org.example.apprestaurant.entity.Order;
import org.example.apprestaurant.entity.OrderItem;
import org.example.apprestaurant.entity.TableEntity;
import org.example.apprestaurant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class OrderController implements Initializable {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DishService dishService;

    @Autowired
    private TableService tableService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ApplicationStateService stateService;

    @FXML
    private TableView<Dish> menuTable;

    @FXML
    private TableColumn<Dish, Integer> dishIdColumn;

    @FXML
    private TableColumn<Dish, String> dishNameColumn;

    @FXML
    private TableColumn<Dish, Integer> dishPriceColumn;

    @FXML
    private TableView<OrderItem> orderItemsTable;

    @FXML
    private TableColumn<OrderItem, String> itemDishColumn;

    @FXML
    private TableColumn<OrderItem, Integer> itemQuantityColumn;

    @FXML
    private TableColumn<OrderItem, Integer> itemPriceColumn;

    @FXML
    private ComboBox<Dish> dishCombo;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField tipsField;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Label orderInfoLabel;

    private ObservableList<Dish> dishList;
    private ObservableList<OrderItem> orderItemsList;
    private Integer currentOrderId;
    private Order currentOrder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dishList = FXCollections.observableArrayList();
        orderItemsList = FXCollections.observableArrayList();
        
        dishIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dishNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dishPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        itemDishColumn.setCellValueFactory(cellData -> {
            Dish dish = cellData.getValue().getDish();
            return new javafx.beans.property.SimpleStringProperty(
                dish != null ? dish.getName() : ""
            );
        });
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceAtOrderTime"));
        
        menuTable.setItems(dishList);
        orderItemsTable.setItems(orderItemsList);
        dishCombo.setItems(dishList);
        
        // Настройка ComboBox для отображения только названия блюда
        dishCombo.setConverter(new javafx.util.StringConverter<Dish>() {
            @Override
            public String toString(Dish dish) {
                return dish != null ? dish.getName() : "";
            }

            @Override
            public Dish fromString(String string) {
                return dishList.stream()
                        .filter(d -> d.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        
        // ID колонка скрыта
        dishIdColumn.setVisible(false);
        
        refreshMenu();
        updateOrderInfo();
    }

    @FXML
    private void visitorArrives() {
        try {
            Integer visitorId = stateService.getCurrentVisitorId();
            if (visitorId == null) {
                showError("Сначала создайте и выберите посетителя");
                return;
            }

            TableEntity table = tableService.findOptimalFreeTable(1);
            if (table == null) {
                showError("Нет свободных столиков");
                return;
            }

            var shift = shiftService.getRandomOpenShift();
            if (shift == null) {
                showError("Нет открытых смен");
                return;
            }

            visitorService.seatVisitor(visitorId, table.getId());
            Order order = orderService.createOrder(visitorId, table.getId(), shift.getId());
            currentOrderId = order.getId();
            currentOrder = order;
            stateService.setCurrentOrderId(currentOrderId);

            showSuccess("Посетитель посажен за столик №" + table.getNumber());
            updateOrderInfo();
            refreshOrderItems();
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void addDishToOrder() {
        if (currentOrderId == null) {
            showError("Сначала посадите посетителя за столик");
            return;
        }

        try {
            Dish selected = dishCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Выберите блюдо");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showError("Количество должно быть больше 0");
                return;
            }

            orderService.addDishToOrder(currentOrderId, selected.getId(), quantity);
            showSuccess("Блюдо добавлено в заказ");
            quantityField.clear();
            refreshOrderItems();
            updateOrderInfo();
        } catch (NumberFormatException e) {
            showError("Некорректное количество");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void finishOrder() {
        if (currentOrderId == null) {
            showError("Нет активного заказа");
            return;
        }

        try {
            Integer visitorId = stateService.getCurrentVisitorId();
            if (visitorId == null) {
                showError("Нет активного посетителя");
                return;
            }

            visitorService.startEating(visitorId);
            showSuccess("Заказ завершён. Спасибо за заказ!");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void payOrder() {
        if (currentOrderId == null) {
            showError("Нет активного заказа");
            return;
        }

        try {
            Integer visitorId = stateService.getCurrentVisitorId();
            if (visitorId == null) {
                showError("Нет активного посетителя");
                return;
            }

            int tips = Integer.parseInt(tipsField.getText().trim());
            visitorService.startPaying(visitorId);
            orderService.closeOrder(currentOrderId, tips);
            showSuccess("Заказ оплачен");
            tipsField.clear();
            currentOrderId = null;
            currentOrder = null;
            stateService.setCurrentOrderId(null);
            orderItemsList.clear();
            updateOrderInfo();
        } catch (NumberFormatException e) {
            showError("Некорректная сумма чаевых");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void visitorLeaves() {
        try {
            Integer visitorId = stateService.getCurrentVisitorId();
            if (visitorId == null) {
                showError("Нет активного посетителя");
                return;
            }

            visitorService.leave(visitorId);
            showSuccess("Посетитель ушёл");
            currentOrderId = null;
            currentOrder = null;
            stateService.clear();
            orderItemsList.clear();
            updateOrderInfo();
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    private void refreshMenu() {
        dishList.clear();
        dishList.addAll(dishService.getAllDishes());
    }

    private void refreshOrderItems() {
        if (currentOrderId == null) {
            orderItemsList.clear();
            return;
        }

        try {
            currentOrder = orderService.getById(currentOrderId);
            orderItemsList.clear();
            if (currentOrder.getItems() != null) {
                orderItemsList.addAll(currentOrder.getItems());
            }
        } catch (Exception e) {
            showError("Ошибка загрузки заказа: " + e.getMessage());
        }
    }

    private void updateOrderInfo() {
        if (currentOrder != null) {
            orderInfoLabel.setText("Заказ ID: " + currentOrder.getId() + 
                " | Сумма: " + currentOrder.getTotalPrice() + " руб.");
            totalPriceLabel.setText("Итого: " + currentOrder.getTotalPrice() + " руб.");
        } else {
            orderInfoLabel.setText("Нет активного заказа");
            totalPriceLabel.setText("Итого: 0 руб.");
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
