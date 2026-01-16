package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.entity.TableEntity;
import org.example.entity.Visitor;
import org.example.repository.TableEntityRepository;
import org.example.repository.VisitorRepository;
import org.example.types.TableStatus;
import org.example.types.VisitorState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final TableEntityRepository tableEntityRepository;

    public Visitor createVisitor(int balance) {
        Visitor visitor = new Visitor();
        visitor.setWalletBalance(balance);
        visitor.setState(VisitorState.WAITING);
        return visitorRepository.save(visitor);
    }

    public List<Visitor> showVisitors() {
        return visitorRepository.findAll();
    }

    public void seatVisitor(int visitorId, int tableId) {
        Visitor visitor = getById(visitorId);
        TableEntity table = tableEntityRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Столик не найден"));

        table.setStatus(TableStatus.OCCUPIED);
        visitor.setTable(table);
        visitor.setState(VisitorState.ORDERING);
    }

    public void startEating(int visitorId) {
        getById(visitorId).setState(VisitorState.EATING);
    }

    public void startPaying(int visitorId) {
        getById(visitorId).setState(VisitorState.PAYING);
    }

    public void leave(int visitorId) {
        Visitor visitor = getById(visitorId);

        TableEntity table = visitor.getTable();
        if (table != null) {
            table.setStatus(TableStatus.FREE);
            visitor.setTable(null);
        }

        visitor.setState(VisitorState.WAITING);
    }

    public Visitor getById(int id) {
        return visitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Посетитель не найден"));
    }
}

