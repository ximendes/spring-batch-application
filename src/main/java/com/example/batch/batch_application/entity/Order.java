package com.example.batch.batch_application.entity;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_ref")
    private String orderRef;

    private BigDecimal amount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    private String note;

    @Transient
    private Date tempOrderDate;

}