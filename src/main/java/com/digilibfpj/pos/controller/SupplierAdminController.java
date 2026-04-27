package com.digilibfpj.pos.controller;

import com.digilibfpj.pos.entity.Supplier;
import com.digilibfpj.pos.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/suppliers")
public class SupplierAdminController {

    private final SupplierRepository supplierRepository;

    public SupplierAdminController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public List<Supplier> findAllSuppliers() {
        return supplierRepository.findAll();
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long supplierId, @RequestBody Supplier supplierRequest) {
        return supplierRepository.findById(supplierId)
                .map(existingSupplier -> {
                    existingSupplier.setName(supplierRequest.getName());
                    existingSupplier.setContactInfo(supplierRequest.getContactInfo());
                    Supplier updatedSupplier = supplierRepository.save(existingSupplier);
                    return ResponseEntity.ok(updatedSupplier);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long supplierId) {
        if (supplierRepository.existsById(supplierId)) {
            supplierRepository.deleteById(supplierId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
