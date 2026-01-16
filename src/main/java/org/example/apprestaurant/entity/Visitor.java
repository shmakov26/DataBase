package org.example.apprestaurant.entity;

import jakarta.persistence.*;
import org.example.apprestaurant.types.VisitorState;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "visitors")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table;

    @Column(name = "wallet_balance", nullable = false)
    private Integer walletBalance;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false,
            name = "state",
            columnDefinition = "visitor_state")
    private VisitorState state;

    @OneToOne(mappedBy = "visitor")
    private Order order;
}


