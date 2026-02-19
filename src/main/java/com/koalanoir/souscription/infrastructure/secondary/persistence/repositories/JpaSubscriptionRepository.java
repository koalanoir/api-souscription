package com.koalanoir.souscription.infrastructure.secondary.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koalanoir.souscription.infrastructure.secondary.persistence.models.SubscriptionEntity;

@Repository
public interface JpaSubscriptionRepository extends JpaRepository<SubscriptionEntity, String>  {
    boolean existsByOfferId(String offerId);
    Optional<SubscriptionEntity> findByOfferId(String offerId);
    
}
