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
@Transactional
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

    public List<Waiter> getWaiters() {
        return waiterRepository.findAll();
    }

    public Waiter getById(int id) {
        return waiterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Официант не найден"));
    }
}

