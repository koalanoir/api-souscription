package com.koalanoir.souscription.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe abstraite de base pour toutes les entités du domaine
 */
public abstract class Entity<ID> {

    protected ID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected Entity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected Entity(ID id) {
        this();
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
