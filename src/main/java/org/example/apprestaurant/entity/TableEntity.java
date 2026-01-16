package org.example.apprestaurant.entity;

import jakarta.persistence.*;
import org.example.apprestaurant.types.TableStatus;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "tables")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false)
    private Integer seats;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,
            name = "status",
            columnDefinition = "table_status")
    private TableStatus status;

    @OneToMany(mappedBy = "table")
    private List<Visitor> visitors;
}


