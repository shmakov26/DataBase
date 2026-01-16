package org.example.apprestaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.apprestaurant.entity.Waiter;
import org.example.apprestaurant.repository.WaiterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WaiterService {

    private final WaiterRepository waiterRepository;

    public Waiter createWaiter(
            String firstName,
            String lastName,
            String middleName,
            String mail,
            String phone,
            int percent
    ) {
        Waiter waiter = new Waiter();
        waiter.setFirstName(firstName);
        waiter.setLastName(lastName);
        waiter.setMiddleName(middleName);
        waiter.setMail(mail);
        waiter.setPhoneNumber(phone);
        waiter.setPercent(percent);
        return waiterRepository.save(waiter);
    }

    @Transactional(readOnly = true)
    public List<Waiter> getWaiters() {
        List<Waiter> waiters = waiterRepository.findAll();
        // Убеждаемся, что все поля загружены, но не трогаем коллекцию shifts
        waiters.forEach(w -> {
            w.getId();
            w.getFirstName();
            w.getLastName();
            w.getMiddleName();
            w.getMail();
            w.getPhoneNumber();
            w.getPercent();
        });
        return waiters;
    }

    public Waiter getById(int id) {
        return waiterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Официант не найден"));
    }

    public void deleteWaiter(int id) {
        if (waiterRepository.findById(id).isPresent()) {
            waiterRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Официант не найден");
        }
    }
}

