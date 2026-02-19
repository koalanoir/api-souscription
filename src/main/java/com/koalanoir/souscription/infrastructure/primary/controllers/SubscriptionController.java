package com.koalanoir.souscription.infrastructure.primary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koalanoir.souscription.application.usecases.CreateSubscriptionCommand;
import com.koalanoir.souscription.application.usecases.CreateSubscriptionUseCase;
import com.koalanoir.souscription.application.usecases.CreateUserCommand;
import com.koalanoir.souscription.application.usecases.CreateUserUseCase;
import com.koalanoir.souscription.domain.models.Subscription;
import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.infrastructure.primary.dtos.CreateSubscriptionRequest;
import com.koalanoir.souscription.infrastructure.primary.dtos.CreateSubscriptionResponse;

/**
 * Adaptateur HTTP d'entrée (Input Adapter)
 * Contrôleur REST pour les opérations de souscription
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "API de gestion des souscriptions")
public class SubscriptionController {



        private final CreateSubscriptionUseCase createSubscriptionUseCase;
        private final CreateUserUseCase createUserUseCase;
        
        public SubscriptionController(CreateSubscriptionUseCase createSubscriptionUseCase, CreateUserUseCase createUserUseCase) {
                this.createSubscriptionUseCase = createSubscriptionUseCase;
                this.createUserUseCase = createUserUseCase;
        }


        /**
         * Créer une nouvelle souscription
         */
        @PostMapping
        @Operation(
                summary = "Créer une nouvelle souscription",
                description = "Crée une nouvelle souscription avec les informations du client fourni"
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Souscription créée avec succès",
                        content = @Content(schema = @Schema(implementation = CreateSubscriptionResponse.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Requête invalide - paramètres manquants ou incorrects"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erreur serveur interne"
                )
        })
        public ResponseEntity<CreateSubscriptionResponse> subscribe(@RequestBody CreateSubscriptionRequest req) {

                CreateUserCommand userCommand = new CreateUserCommand(
                        req.clientName(),
                        req.phoneNumber(),
                        req.email()
                );

                CreateSubscriptionCommand subscriptionCommand = new CreateSubscriptionCommand(
                        req.offerId(),
                        req.subscriptionType()
                );

                User user = createUserUseCase.handle(userCommand);
                Subscription sub = createSubscriptionUseCase.handle(subscriptionCommand, user);
                return ResponseEntity.ok(CreateSubscriptionResponse.fromDomain(sub, user));
        }

    
}
