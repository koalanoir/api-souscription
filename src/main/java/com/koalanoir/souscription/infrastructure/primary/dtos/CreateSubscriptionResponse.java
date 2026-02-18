package com.koalanoir.souscription.infrastructure.primary.dtos;

import org.jspecify.annotations.Nullable;

import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.models.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

/**
 * DTO pour la réponse de création de souscription
 */

@Schema(description = "Réponse à la création d'une souscription")
public record CreateSubscriptionResponse(

    @Schema(description = "Identifiant unique de la souscription", example = "550e8400-e29b-41d4-a716-446655440000")
    String id,

    @Schema(description = "Identifiant de l'offre", example = "OFFRE_PREMIUM")
    String offerId,

    @Schema(description = "Identifiant du client", example = "client123")
    String clientId,

    @Schema(description = "Nom du client", example = "Jean Dupont")
    String clientName,

    @Schema(description = "Email du client", example = "jean.dupont@example.com")
    String email,


    @Schema(description = "Date de création", example = "2026-02-10T10:30:00Z")
    String createdAt,

    @Schema(description = "Statut de la souscription", example = "ACTIVE")
    String status

) {

    public static CreateSubscriptionResponse fromDomain(Subscription sub, User user) {
        return new CreateSubscriptionResponse(
                sub.getId(),
                sub.getOfferId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                sub.getCreatedAt().toString(),
                sub.getStatus().name()
        );
    }


}
