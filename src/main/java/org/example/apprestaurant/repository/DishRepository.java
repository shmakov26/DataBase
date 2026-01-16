package org.example.apprestaurant.repository;

import org.example.apprestaurant.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
//    @Query("SELECT d FROM Dish d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Dish> findByNameContainingIgnoreCase(@Param("name") String name);
}
