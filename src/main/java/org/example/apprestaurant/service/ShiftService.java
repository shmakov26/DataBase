package org.example.apprestaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.apprestaurant.entity.Shift;
import org.example.apprestaurant.entity.Waiter;
import org.example.apprestaurant.repository.ShiftRepository;
import org.example.apprestaurant.repository.WaiterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final WaiterRepository waiterRepository;

    public void openShift(Integer waiterId) {

        Waiter waiter = waiterRepository.findById(waiterId)
                .orElseThrow(() -> new EntityNotFoundException("Официант не найден"));

        // Проверка: нет ли уже активной смены
        boolean hasActiveShift = shiftRepository
                .existsByWaiterAndShiftEndIsNull(waiter);

        if (hasActiveShift) {
            throw new IllegalStateException("У официанта уже есть открытая смена");
        }

        Shift shift = new Shift();
        shift.setWaiter(waiter);
        shift.setShiftStart(LocalDateTime.now());
        shift.setPantsCollected(0);
        shift.setTipsCollected(0);
        shift.setTotalOrdersAmount(0);

        shiftRepository.save(shift);
    }

    public void closeShift(int shiftId) {
        Shift shift = getById(shiftId);
        shift.setShiftEnd(LocalDateTime.now());
    }

    public Shift getById(int id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Смена не найдена"));
    }

    @Transactional(readOnly = true)
    public List<Shift> getOpenShifts() {
        List<Shift> shifts = shiftRepository.findByShiftEndIsNull();
        // Принудительно инициализируем все поля Waiter в рамках транзакции
        shifts.forEach(s -> {
            if (s.getWaiter() != null) {
                // Доступ к полям для инициализации прокси
                s.getWaiter().getId();
                s.getWaiter().getFirstName();
                s.getWaiter().getLastName();
            }
        });
        return shifts;
    }

    @Transactional(readOnly = true)
    public List<Shift> getShiftsByWaiter(int waiterId) {
        List<Shift> shifts = shiftRepository.findByWaiterId(waiterId);
        // Принудительно инициализируем все поля Waiter в рамках транзакции
        shifts.forEach(s -> {
            if (s.getWaiter() != null) {
                // Доступ к полям для инициализации прокси
                s.getWaiter().getId();
                s.getWaiter().getFirstName();
                s.getWaiter().getLastName();
            }
        });
        return shifts;
    }

    public Shift getRandomOpenShift() {
        List<Shift> shifts = getOpenShifts();
        if (shifts.isEmpty()) {
            throw new IllegalStateException("Нет открытых смен");
        }
        return shifts.get(new Random().nextInt(shifts.size()));
    }
}

