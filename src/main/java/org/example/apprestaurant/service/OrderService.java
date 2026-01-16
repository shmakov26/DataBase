package org.example.apprestaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.apprestaurant.entity.*;
import org.example.apprestaurant.repository.*;
import org.example.apprestaurant.types.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final ShiftRepository shiftRepository;
    private final VisitorRepository visitorRepository;

    public Order createOrder(int visitorId, int tableId, int shiftId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new EntityNotFoundException("Посетитель не найден"));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Смена не найдена"));

        Order order = new Order();
        order.setVisitor(visitor);
        order.setTable(visitor.getTable());
        order.setShift(shift);
        order.setStatus(OrderStatus.OPEN);
        order.setTotalPrice(0);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public void addDishToOrder(int orderId, int dishId, int qty) {
        Order order = getById(orderId);
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setDish(dish);
        item.setQuantity(qty);
        item.setPriceAtOrderTime(dish.getPrice());

        order.setTotalPrice(order.getTotalPrice() + dish.getPrice() * qty);
        orderItemRepository.save(item);
    }

    public void closeOrder(int orderId, int tips) {
        Order order = getById(orderId);
        Visitor visitor = order.getVisitor();
        Shift shift = order.getShift();

        int total = order.getTotalPrice() + tips;

        if (visitor.getWalletBalance() < total) {
            shift.setPantsCollected(shift.getPantsCollected() + 1);
        } else {
            visitor.setWalletBalance(visitor.getWalletBalance() - total);
        }

        shift.setTipsCollected(shift.getTipsCollected() + tips);
        shift.setTotalOrdersAmount(shift.getTotalOrdersAmount() + total);

        order.setStatus(OrderStatus.CLOSED);
        order.setClosedAt(LocalDateTime.now());
    }

    public Order getByVisitorId(int visitorId) {
        return orderRepository.findByVisitorId(visitorId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));
    }

    public Order getById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));
    }
}
