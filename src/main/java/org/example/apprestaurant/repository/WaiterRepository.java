package org.example.repository;

import org.example.entity.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WaiterRepository extends JpaRepository<Waiter, Integer> {
//    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Waiter w WHERE w.mail = :mail")
    boolean existsByMail(@Param("mail") String mail);

//    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Waiter w WHERE w.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
