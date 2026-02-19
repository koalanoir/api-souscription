package com.koalanoir.souscription.domain.models;

import lombok.Getter;

@Getter
public final class Subscription extends Entity<String> {

    private String userId;
    private String offerId;
    private SubscriptionStatus status;

    public Subscription(String id, String userId, String offerId, SubscriptionStatus status) {
        super(id);
        this.userId = userId;
        this.offerId = offerId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return (o instanceof Subscription that) && this.id.equals(that.id);
    }
    @Override
    public int hashCode() { return id.hashCode(); }
    
}
