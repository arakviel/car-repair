package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;
import java.util.UUID;

public class Workroom extends BaseDomain<UUID> implements Domain {

    private Address address;
    private String name;
    private byte[] photo;
    private String description;

    private Workroom(Address address, String name, byte[] photo) {
        this.address = address;
        this.name = name;
        this.photo = photo;
    }

    public static WorkroomBuilderAddress builder() {
        return address -> name -> photo -> () -> new Workroom(address, name, photo);
    }

    @FunctionalInterface
    public interface WorkroomBuilderAddress {
        WorkroomBuilderName address(Address address);
    }

    @FunctionalInterface
    public interface WorkroomBuilderName {
        WorkroomBuilderPhoto name(String name);
    }

    @FunctionalInterface
    public interface WorkroomBuilderPhoto {
        WorkroomBuilder photo(byte[] photo);
    }

    @FunctionalInterface
    public interface WorkroomBuilder {
        Workroom build();
    }

    public Address getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Workroom.class.getSimpleName() + "[", "]")
                .add("address=" + address)
                .add("name='" + name + "'")
                .add("photo=" + photo)
                .add("description='" + description + "'")
                .add("id=" + id)
                .toString();
    }
}
