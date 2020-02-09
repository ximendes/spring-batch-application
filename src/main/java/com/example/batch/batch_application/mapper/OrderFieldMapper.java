package com.example.batch.batch_application.mapper;

import com.example.batch.batch_application.entity.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class OrderFieldMapper implements FieldSetMapper<Order> {

    @Override
    public Order mapFieldSet(FieldSet fieldSet) {
        Order order = new Order();
        order.setOrderRef(fieldSet.readString("order_ref"));
        order.setAmount(fieldSet.readBigDecimal("amount"));
        order.setNote(fieldSet.readString("note"));
        order.setTempOrderDate(fieldSet.readDate("order_date"));
        return order;
    }
}
