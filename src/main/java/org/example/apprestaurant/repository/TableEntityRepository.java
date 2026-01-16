package org.example.repository;

import org.example.entity.TableEntity;
import org.example.types.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, Integer> {
//    @Query("SELECT t.number, t.seats FROM TableEntity t WHERE t.status = :status")
    List<TableEntity> findByStatus(@Param("status") TableStatus status);

    @Modifying
//    @Query("DELETE FROM TableEntity t WHERE t.number = :number")
    void deleteByNumber(@Param("number") Integer number);
}
