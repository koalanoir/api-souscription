package com.koalanoir.souscription.application.usecases;

/**
 * Modèle d'entrée applicatif pour la création d'une souscription.
 * Indépendant de la couche HTTP.
 */
public record CreateSubscriptionCommand(
        String offerId,
        String subscriptionType
) {}
