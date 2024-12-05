package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.Service;
import com.example.stockmanagement.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceManagementService serviceManagementService;

    @GetMapping()
    public Page<Service> getServices(Pageable pageable) {
        return serviceManagementService.getServices(pageable);
    }

    @PostMapping
    public Service addService(@RequestBody Service service) {
        return serviceManagementService.addService(service);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}