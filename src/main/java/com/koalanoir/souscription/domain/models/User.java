package com.koalanoir.souscription.domain.models;



import lombok.Getter;


@Getter
public final class User extends Entity<String>  {

    private String name;
    private String phone;
    private String email;

    public User(String id, String name, String phone, String email) {
        super(id);
        this.name = name;
        this.phone = phone;
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return (o instanceof User that) && this.id.equals(that.id);
    }
    @Override
    public int hashCode() { return id.hashCode(); }

}
