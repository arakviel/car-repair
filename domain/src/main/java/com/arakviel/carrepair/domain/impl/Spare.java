package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;
import java.util.UUID;

public class Spare extends BaseDomain<UUID> implements Domain {

    private Workroom workroom;
    private String name;
    private String description;
    private byte[] photo;
    private Money price;
    private int quantityInStock;
    // не хороший варіант
    private int extraFieldQuantity;

    private Spare(Workroom workroom, String name, Money price, int quantityInStock) {
        this.workroom = workroom;
        this.name = name;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public static SpareBuilderWorkroom builder() {
        return workroom -> name -> price -> quantityInStock -> () -> new Spare(workroom, name, price, quantityInStock);
    }

    @FunctionalInterface
    public interface SpareBuilderWorkroom {
        SpareBuilderName workroom(Workroom workroom);
    }

    @FunctionalInterface
    public interface SpareBuilderName {
        SpareBuilderPrice name(String name);
    }

    @FunctionalInterface
    public interface SpareBuilderPrice {
        SpareBuilderQuantityInStock price(Money price);
    }

    @FunctionalInterface
    public interface SpareBuilderQuantityInStock {
        SpareBuilder quantityInStock(int quantityInStock);
    }

    @FunctionalInterface
    public interface SpareBuilder {
        Spare build();
    }

    public Workroom getWorkroom() {
        return workroom;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getExtraFieldQuantity() {
        return extraFieldQuantity;
    }

    public void setExtraFieldQuantity(int extraFieldQuantity) {
        this.extraFieldQuantity = extraFieldQuantity;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Spare.class.getSimpleName() + "[", "]")
                .add("workroom=" + workroom)
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("photo=" + photo)
                .add("price=" + price)
                .add("quantityInStock=" + quantityInStock)
                .add("extraFieldQuantity='" + extraFieldQuantity + "'")
                .add("id=" + id)
                .toString();
    }
}
