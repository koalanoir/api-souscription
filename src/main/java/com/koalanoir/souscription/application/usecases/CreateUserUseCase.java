package com.koalanoir.souscription.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.domain.repository.UserRepository;
import com.koalanoir.souscription.infrastructure.primary.dtos.CreateSubscriptionRequest;

@Service
public class CreateUserUseCase {

    private final UserRepository repo;

    public CreateUserUseCase(UserRepository repo) {
        this.repo = repo;
    }

    public User create(String phone,String email, String name) {    
        if (repo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User(UUID.randomUUID().toString(), name, phone, email);
        return repo.save(user);
    }

    public User handle(CreateSubscriptionRequest req) {
        return create(req.phoneNumber(), req.email(), req.clientName());
    }
}