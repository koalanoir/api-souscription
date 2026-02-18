package com.koalanoir.souscription.infrastructure.primary.dtos;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO pour la requête de création de souscription
 */

@Schema(description = "Requête de création d'une souscription")
public record CreateSubscriptionRequest(

    @Schema(description = "Identifiant de l'offre", example = "OFFRE_PREMIUM")
    String offerId,
    

    @Schema(description = "Nom du client", example = "Jean Dupont")
    String clientName,

    @Schema(description = "Email du client", example = "jean.dupont@example.com")
    String email,

    @Schema(description = "Numéro de téléphone", example = "+33612345678")
    String phoneNumber,

    @Schema(description = "Type de souscription", example = "PREMIUM")
    String subscriptionType
) {}   
