package com.koalanoir.souscription.domain.repository;

import java.util.List;
import java.util.Optional;

import com.koalanoir.souscription.domain.models.User;

public interface UserRepository {
    User save(User user);
    void deleteById(String id);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
}
