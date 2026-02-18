package com.koalanoir.souscription.infrastructure.secondary.persistence.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koalanoir.souscription.infrastructure.secondary.persistence.models.UserEntity;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
 
    
