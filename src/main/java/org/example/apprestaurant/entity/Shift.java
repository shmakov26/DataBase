package org.example.apprestaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiter_id", nullable = false)
    private Waiter waiter;

    @Column(name = "pants_collected", nullable = false)
    private Integer pantsCollected;

    @Column(name = "tips_collected", nullable = false)
    private Integer tipsCollected;

    @Column(name = "total_orders_amount", nullable = false)
    private Integer totalOrdersAmount;

    @Column(name = "shift_start", nullable = false)
    private LocalDateTime shiftStart;

    @Column(name = "shift_end")
    private LocalDateTime shiftEnd;

    @OneToMany(mappedBy = "shift")
    private List<Order> orders;
}

