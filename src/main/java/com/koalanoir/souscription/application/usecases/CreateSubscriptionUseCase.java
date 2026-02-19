package com.koalanoir.souscription.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.models.SubscriptionStatus;
import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.domain.repository.SubscriptionRepository;

@Service
public class CreateSubscriptionUseCase {
    private final SubscriptionRepository repo;

    public CreateSubscriptionUseCase(SubscriptionRepository repo) {
        this.repo = repo;
    }

    public Subscription create(String userid,String offerid, SubscriptionStatus status) {
        if (repo.findById(userid).isPresent()) {
            throw new IllegalArgumentException("Subscription already exists");
        }

        Subscription subscription = new Subscription(UUID.randomUUID().toString(), userid, offerid, status);
        return repo.save(subscription);
    }

    public Subscription handle(CreateSubscriptionCommand command, User user) {
        return create(user.getId(), command.offerId(), SubscriptionStatus.ACTIVE);
    }
}
