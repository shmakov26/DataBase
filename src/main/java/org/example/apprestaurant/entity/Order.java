package org.example.apprestaurant.entity;

import jakarta.persistence.*;
import org.example.apprestaurant.types.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "visitor_id", nullable = false, unique = true)
    private Visitor visitor;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,
            name = "status",
            columnDefinition = "order_status")
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}


