package com.koalanoir.souscription.infrastructure.secondary.persistence.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.repository.SubscriptionRepository;
import com.koalanoir.souscription.infrastructure.secondary.persistence.mappers.SubscriptionMapper;
import com.koalanoir.souscription.infrastructure.secondary.persistence.models.SubscriptionEntity;

/**
 * Adaptateur de persistance pour les Souscriptions
 * Implémentation concrète du port de sortie
 */
@Repository
@Transactional
public class SubscriptionRepositoryPostgres implements SubscriptionRepository  {
    private final JpaSubscriptionRepository jpa;

    public SubscriptionRepositoryPostgres(JpaSubscriptionRepository jpa) {
        this.jpa = jpa;
    }

    // -------- Commands --------

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = SubscriptionMapper.toEntity(subscription, LocalDate.now());
        SubscriptionEntity saved = jpa.save(entity);
        return SubscriptionMapper.toDomain(saved);
    }

    @Override
    public void deleteById(String id) {
        jpa.deleteById(id);
    }

    // -------- Queries (read-only) --------

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscription> findById(String id) {
        return jpa.findById(id).map(SubscriptionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscription> findByOfferId(String offerId) {
        return jpa.findByOfferId(offerId).map(SubscriptionMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public boolean existsByOfferId(String offerId) {
        return jpa.existsByOfferId(offerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> findAll() {
        return jpa.findAll().stream().map(SubscriptionMapper::toDomain).toList();
    }
}
