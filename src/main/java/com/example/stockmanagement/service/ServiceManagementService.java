package com.example.stockmanagement.service;

import com.example.stockmanagement.model.Service;
import com.example.stockmanagement.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementService {
    private final ServiceRepository serviceRepository;

    public Page<Service> getServices(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    @Transactional
    public Service addService(Service service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}