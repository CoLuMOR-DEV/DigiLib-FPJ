package com.digilibfpj.pos.repository;

import com.digilibfpj.pos.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBook_BookId(Long bookId);

    @Query("SELECT i FROM Inventory i WHERE i.stockQty <= i.lowAlertQty")
    List<Inventory> findLowStockItems();
}
