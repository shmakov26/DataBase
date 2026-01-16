package org.example.apprestaurant.service;

import org.springframework.stereotype.Service;

@Service
public class ApplicationStateService {
    
    private Integer currentVisitorId;
    private Integer currentOrderId;

    public Integer getCurrentVisitorId() {
        return currentVisitorId;
    }

    public void setCurrentVisitorId(Integer currentVisitorId) {
        this.currentVisitorId = currentVisitorId;
    }

    public Integer getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(Integer currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public void clear() {
        this.currentVisitorId = null;
        this.currentOrderId = null;
    }
}
