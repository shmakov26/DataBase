package org.example.apprestaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.apprestaurant.entity.TableEntity;
import org.example.apprestaurant.types.TableStatus;
import org.example.apprestaurant.repository.TableEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {

    private final TableEntityRepository tableRepository;

    public void createTable(int number, int seats) {
        TableEntity table = new TableEntity();
        table.setNumber(number);
        table.setSeats(seats);
        table.setStatus(TableStatus.FREE);
        tableRepository.save(table);
    }

    public void deleteTable(int id) {
        tableRepository.deleteById(id);
    }

    public List<TableEntity> getFreeTables() {
        return tableRepository.findByStatus(TableStatus.FREE);
    }

    public TableEntity findOptimalFreeTable(int people) {
        return getFreeTables().stream()
                .filter(t -> t.getSeats() >= people)
                .min(Comparator.comparingInt(TableEntity::getSeats))
                .orElseThrow(() -> new IllegalStateException("Нет подходящего столика"));
    }

    public TableEntity getById(int id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Столик не найден"));
    }
}

