package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class ClientEntity extends BaseEntity<UUID> implements Entity {

    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private byte[] photo;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public ClientEntity(UUID id, String phone, String email) {
        super(id);
        this.phone = phone;
        this.email = email;
    }

    public static ClientEntityBuilderId builder() {
        return id -> phone -> email -> () -> new ClientEntity(id, phone, email);
    }

    @FunctionalInterface
    public interface ClientEntityBuilderId {
        ClientEntityBuilderPhone id(UUID id);
    }

    @FunctionalInterface
    public interface ClientEntityBuilderPhone {
        ClientEntityBuilderEmail phone(String phone);
    }

    @FunctionalInterface
    public interface ClientEntityBuilderEmail {
        ClientEntityBuilder email(String email);
    }

    @FunctionalInterface
    public interface ClientEntityBuilder {
        ClientEntity build();
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ClientEntity.class.getSimpleName() + "[", "]")
                .add("phone='" + phone + "'")
                .add("email='" + email + "'")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("middleName='" + middleName + "'")
                .add("photo=" + photo)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("id=" + id)
                .toString();
    }
}
