package org.example;

import org.example.entity.*;
import org.example.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Scanner;

@Component
public class App implements CommandLineRunner {

    private final DishService dishService;
    private final WaiterService waiterService;
    private final ShiftService shiftService;
    private final TableService tableService;
    private final VisitorService visitorService;
    private final OrderService orderService;

    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    private Integer currentVisitorId;
    private Integer currentOrderId;

    public App(
            DishService dishService,
            WaiterService waiterService,
            ShiftService shiftService,
            TableService tableService,
            VisitorService visitorService,
            OrderService orderService
    ) {
        this.dishService = dishService;
        this.waiterService = waiterService;
        this.shiftService = shiftService;
        this.tableService = tableService;
        this.visitorService = visitorService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {

        while (true) {
            printMenu();
            int cmd = readInt("Выберите команду: ");

            try {
                switch (cmd) {
                    case 1 -> addDish();
                    case 2 -> deleteDish();
                    case 3 -> showMenu();
                    case 4 -> openShift();
                    case 5 -> closeShift();
                    case 6 -> showOpenShifts();
                    case 7 -> showWaiterShifts();
                    case 8 -> createTable();
                    case 9 -> deleteTable();
                    case 10 -> showFreeTables();
                    case 11 -> visitorArrives();
                    case 12 -> addDishesToOrder();
                    case 13 -> payOrder();
                    case 14 -> visitorLeaves();
                    case 15 -> showWaiters();
                    case 16 -> createWaiter();
                    case 17 -> replaceCurrentVisitor();
                    case 0 -> {
                        System.out.println("Выход...");
                        System.exit(0);
                    }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /* ================== МЕНЮ ================== */

    private void printMenu() {
        System.out.println("""
                
                ===== МЕНЮ =====
                1  - Добавить блюдо
                2  - Удалить блюдо
                3  - Показать меню
                4  - Открыть смену
                5  - Закрыть смену
                6  - Показать открытые смены
                7  - Показать смены официанта
                8  - Создать столик
                9  - Удалить столик
                10 - Показать свободные столики
                11 - Приход посетителя
                12 - Добавить блюдо в заказ
                13 - Оплата заказа
                14 - Уход посетителя
                15 - Все официанты
                16 - Добавить официанта
                17 - Смена текущего посетителя
                0  - Выход
                """);
    }

    /* ================== БЛЮДА ================== */

    private void addDish() {
        String name = readString("Название: ");
        int price = readInt("Цена (р): ");
        String ingredients = readString("Состав: ");
        int weight = readInt("Вес (г): ");

        dishService.createDish(name, price, ingredients, weight);
        System.out.println("Блюдо добавлено");
    }

    private void deleteDish() {
        showMenu();
        int id = readInt("ID блюда: ");
        dishService.deleteDish(id);
        System.out.println("Блюдо удалено");
    }

    private void showMenu() {
        dishService.getAllDishes()
                .forEach(d -> System.out.println(
                        d.getId() + " | " + d.getName() + " | " + d.getPrice()
                ));
    }

    /* ================== СМЕНЫ ================== */
    private void createWaiter() {
        String firstName = readString("Имя: ");
        String lastName = readString("Фамилия: ");
        String middleName = readString("Отчество: ");
        String mail = readString("Почта: ");
        String phone = readString("Телефон: ");
        int percent = readInt("Процентная ставка: ");
        waiterService.createWaiter(firstName, lastName, middleName, mail, phone, percent);
    }

    private void showWaiters() {
        waiterService.getWaiters()
                .forEach(System.out::println);
    }

    private void openShift() {
        int waiterId = readInt("ID официанта: ");
        shiftService.openShift(waiterId);
        System.out.println("Смена открыта");
    }

    private void closeShift() {
        int shiftId = readInt("ID смены: ");
        shiftService.closeShift(shiftId);
        System.out.println("Смена закрыта");
    }

    private void showOpenShifts() {
        shiftService.getOpenShifts()
                .forEach(s -> System.out.println(
                        "Смена " + s.getId() + " | Официант " + s.getWaiter().getLastName()
                ));
    }

    private void showWaiterShifts() {
        int waiterId = readInt("ID официанта: ");
        shiftService.getShiftsByWaiter(waiterId)
                .forEach(s -> System.out.println(
                        "Смена " + s.getId() + " | Старт " + s.getShiftStart()
                ));
    }

    /* ================== СТОЛИКИ ================== */

    private void createTable() {
        int number = readInt("Номер столика: ");
        int seats = readInt("Мест: ");
        tableService.createTable(number, seats);
        System.out.println("Столик создан");
    }

    private void deleteTable() {
        int id = readInt("ID столика: ");
        tableService.deleteTable(id);
        System.out.println("Столик удалён");
    }

    private void showFreeTables() {
        tableService.getFreeTables()
                .forEach(t -> System.out.println(
                        "ID " + t.getId() + " | №" + t.getNumber() + " | мест " + t.getSeats()
                ));
    }

    /* ================== ПОСЕТИТЕЛЬ ================== */

    private void visitorArrives() {
        int balance = readInt("Баланс посетителя: ");
        Visitor visitor = visitorService.createVisitor(balance);
        currentVisitorId = visitor.getId();

        TableEntity table = tableService.findOptimalFreeTable(1);
        if (table == null) {
            throw new IllegalStateException("Нет свободных столиков");
        }

        Shift shift = shiftService.getRandomOpenShift();
        if (shift == null) {
            throw new IllegalStateException("Нет открытых смен");
        }

        visitorService.seatVisitor(currentVisitorId, table.getId());

        Order order = orderService.createOrder(
                currentVisitorId,
                table.getId(),
                shift.getId()
        );
        currentOrderId = order.getId();

        System.out.println("Посетитель посажен за столик №" + table.getNumber());
    }

    private void addDishesToOrder() {
        if (currentVisitorId == null) {
            throw new IllegalStateException("Нет активного посетителя");
        }
        if (currentOrderId == null) {
            throw new IllegalStateException("Нет активного заказа");
        }
        addDishToOrder();
        while (true){
            String flag = readString("Вы хотите добавить ещё блюдо?");
            if ("да".equalsIgnoreCase(flag)) {
                addDishToOrder();
            } else if ("нет".equalsIgnoreCase(flag)) {
                visitorService.startEating(currentVisitorId);
                System.out.println("Спасибо за заказ");
                break;
            } else {
                System.out.println("Не понял, повторите ещё раз");
            }
        }
    }

    private void addDishToOrder() {
        showMenu();
        int dishId = readInt("ID блюда: ");
        int qty = readInt("Количество: ");
        orderService.addDishToOrder(currentOrderId, dishId, qty);
        System.out.println("Блюдо добавлено.");
    }

    private void payOrder() {
        visitorService.startPaying(currentVisitorId);
        int tips = readInt("Чаевые: ");
        orderService.closeOrder(currentOrderId, tips);
        System.out.println("Заказ оплачен");
    }

    private void visitorLeaves() {
        visitorService.leave(currentVisitorId);
        currentVisitorId = null;
        currentOrderId = null;
        System.out.println("Посетитель ушёл");
    }

    private void replaceCurrentVisitor() {
        visitorService.showVisitors()
                .forEach(v -> System.out.println("Id " + v.getId() + " | Статус " + v.getState()));
        int visitorId = readInt("Id обслуживаемого посетителя: ");
        Order order = orderService.getByVisitorId(visitorId);
        if (order == null) {
            throw new IllegalStateException("У посетителя нет активного заказа");
        }
        currentVisitorId = visitorId;
        currentOrderId = order.getId();
        System.out.println("Посетитель успешно сменён");
    }

    /* ================== УТИЛИТЫ ================== */

    private int readInt(String msg) {
        System.out.print(msg);
        return Integer.parseInt(scanner.nextLine());
    }

    private String readString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
}
