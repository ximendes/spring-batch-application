package com.example.batch.batch_application.repository;

import com.example.batch.batch_application.entity.Order;
import com.example.batch.batch_application.entity.Referencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenciaRepository extends JpaRepository<Referencia, Long> {
}
