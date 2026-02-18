package com.koalanoir.souscription.infrastructure.secondary.persistence.mappers;

import java.time.LocalDate;

import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.infrastructure.secondary.persistence.models.SubscriptionEntity;

public class SubscriptionMapper {
    public static SubscriptionEntity toEntity(Subscription subscription, LocalDate subscribedAt) {
        return new SubscriptionEntity(
            subscription.getId(),
            subscription.getUserId(),
            subscription.getOfferId(),
            subscribedAt,
            subscription.getStatus()
        );
    }
    
    public static Subscription toDomain(SubscriptionEntity e) {
        return new Subscription(
            e.getId(),
            e.getUserId(),
            e.getOfferId(),
            e.getStatus()
        );
    }
}
