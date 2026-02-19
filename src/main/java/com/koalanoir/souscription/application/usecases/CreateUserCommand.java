package com.koalanoir.souscription.application.usecases;

/**
 * Modèle d'entrée applicatif pour la création d'un utilisateur.
 * Indépendant de la couche HTTP.
 */
public record CreateUserCommand(
        String name,
        String phone,
        String email
) {}
