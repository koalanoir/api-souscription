package com.koalanoir.souscription.domain.repository;

import java.util.List;
import java.util.Optional;

import com.koalanoir.souscription.domain.models.Subscription;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);
    void deleteById(String id);
    Optional<Subscription> findById(String id);
    Optional<Subscription> findByOfferId(String offerId);
    List<Subscription> findAll();
    
}
