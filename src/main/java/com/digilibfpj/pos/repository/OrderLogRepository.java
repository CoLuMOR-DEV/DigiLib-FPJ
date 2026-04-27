package com.digilibfpj.pos.repository;

import com.digilibfpj.pos.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
}
