package com.koalanoir.souscription.infrastructure.secondary.persistence.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@AllArgsConstructor
@Getter
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "offer_id")
    private String offerId;

    @Column(name = "subscribed_at")
    private LocalDate subscribedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

}