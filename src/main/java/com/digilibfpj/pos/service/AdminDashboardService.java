package com.digilibfpj.pos.service;

import com.digilibfpj.pos.entity.Inventory;
import com.digilibfpj.pos.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final InventoryRepository inventoryRepository;

    public AdminDashboardService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getLowStockAlerts() {
        return inventoryRepository.findLowStockItems();
    }
}
