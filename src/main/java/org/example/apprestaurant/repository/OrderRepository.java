package org.example.apprestaurant.repository;

import org.example.apprestaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
//    @Query("SELECT o FROM Order o WHERE o.visitor.id = :visitorId")
    Optional<Order> findByVisitorId(@Param("visitorId") Integer visitorId);
}
