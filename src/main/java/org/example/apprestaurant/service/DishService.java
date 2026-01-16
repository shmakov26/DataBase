package org.example.apprestaurant.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.apprestaurant.entity.Dish;
import org.example.apprestaurant.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DishService {

    private final DishRepository dishRepository;

    public Dish createDish(String name, int price, String ingredients, int weight) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setPrice(price);
        dish.setIngredients(ingredients);
        dish.setWeightGrams(weight);
        return dishRepository.save(dish);
    }

    public void deleteDish(int id) {
        if (dishRepository.findById(id).isPresent()) dishRepository.deleteById(id);
                else throw new EntityNotFoundException("Блюдо не найдено");
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public List<Dish> searchDishesByName(String name) {
        return dishRepository.findByNameContainingIgnoreCase(name);
    }

    public Dish getById(int id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо не найдено"));
    }
}


