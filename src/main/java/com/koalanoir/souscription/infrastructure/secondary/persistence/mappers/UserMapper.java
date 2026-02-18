package com.koalanoir.souscription.infrastructure.secondary.persistence.mappers;

import com.koalanoir.souscription.domain.models.User;
import com.koalanoir.souscription.infrastructure.secondary.persistence.models.UserEntity;


public final class UserMapper {

    public static UserEntity toEntity(User user) {
        return new UserEntity(user);
    }

    public static User toDomain(UserEntity e) {
        return new User(
            e.getId(),
            e.getName(),
            e.getPhone(),
            e.getEmail()
        );
    }
}