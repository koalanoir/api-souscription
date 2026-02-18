package com.koalanoir.souscription.infrastructure.secondary.persistence.repositories;



import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.domain.repository.UserRepository;
import com.koalanoir.souscription.infrastructure.secondary.persistence.mappers.UserMapper;
import com.koalanoir.souscription.infrastructure.secondary.persistence.models.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Adaptateur de persistance pour les Utilisateurs
 * Implémentation concrète du port de sortie
 */
@Repository
@Transactional // méthodes en écriture
public class UserRepositoryPostgres implements UserRepository {

    private final JpaUserRepository jpa;

    public UserRepositoryPostgres(JpaUserRepository jpa) {
        this.jpa = jpa;
    }

    // -------- Commands --------

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = jpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public void deleteById(String id) {
        jpa.deleteById(id);
    }

    // -------- Queries (read-only) --------

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return jpa.findById(id).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return jpa.findAll().stream().map(UserMapper::toDomain).toList();
    }
}