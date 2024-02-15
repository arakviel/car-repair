package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class Client extends BaseDomain<UUID> implements Domain {

    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private byte[] photo;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Client(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public static ClientBuilderPhone builder() {
        return phone -> email -> () -> new Client(phone, email);
    }

    @FunctionalInterface
    public interface ClientBuilderPhone {
        ClientBuilderEmail phone(String phone);
    }

    @FunctionalInterface
    public interface ClientBuilderEmail {
        ClientBuilder email(String email);
    }

    @FunctionalInterface
    public interface ClientBuilder {
        Client build();
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

    public String getFullName() {
        return "%s %s %s".formatted(firstName, lastName, middleName);
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
        return new StringJoiner(", ", Client.class.getSimpleName() + "[", "]")
                .add("phone='" + phone + "'")
                .add("email='" + email + "'")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("middleName='" + middleName + "'")
                .add("fullName='" + fullName + "'")
                .add("photo=" + photo)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("id=" + id)
                .toString();
    }
}
