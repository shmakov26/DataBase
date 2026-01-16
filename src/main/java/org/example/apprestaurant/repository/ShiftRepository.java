package org.example.apprestaurant.repository;

import org.example.apprestaurant.entity.Shift;
import org.example.apprestaurant.entity.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
//    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shift s WHERE s.waiter = :waiter AND s.shiftEnd IS NULL")
    boolean existsByWaiterAndShiftEndIsNull(@Param("waiter") Waiter waiter);

//    @Query("SELECT s FROM Shift s WHERE s.shiftEnd IS NULL")
    List<Shift> findByShiftEndIsNull();

//    @Query("SELECT s FROM Shift s WHERE s.waiter.id = :id")
    List<Shift> findByWaiterId(@Param("id") Integer id);
}
