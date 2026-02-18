package com.koalanoir.souscription.infrastructure.secondary.persistence.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.koalanoir.souscription.domain.models.User;

import jakarta.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor
public class UserEntity  {

    @Id
    private String id;
    private String name;
    private String phone;
    private String email;

    public UserEntity(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
    }

}
