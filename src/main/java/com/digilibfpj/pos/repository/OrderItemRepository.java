package com.digilibfpj.pos.repository;

import com.digilibfpj.pos.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
