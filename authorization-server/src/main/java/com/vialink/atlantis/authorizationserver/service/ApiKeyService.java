package com.vialink.atlantis.authorizationserver.service;

import com.vialink.atlantis.authorizationserver.apikey.ApiKey;
import com.vialink.atlantis.authorizationserver.apikey.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository repository;

    public Optional<ApiKey> get(UUID id) {
        return repository.findById(id);
    }

    public Optional<ApiKey> findByApplicationName(String name) {
        return repository.findByApplicationName(name);
    }

}
