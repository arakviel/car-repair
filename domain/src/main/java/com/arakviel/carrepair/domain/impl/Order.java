package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import com.arakviel.carrepair.domain.proxy.Services;
import com.arakviel.carrepair.domain.proxy.ServicesProxy;
import com.arakviel.carrepair.domain.proxy.Spares;
import com.arakviel.carrepair.domain.proxy.SparesProxy;
import com.arakviel.carrepair.domain.proxy.Staff;
import com.arakviel.carrepair.domain.proxy.StaffProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class Order extends BaseDomain<UUID> implements Domain {

    private Client client;
    private Car car;
    private Discount discount;
    private Money price;
    private PaymentType paymentType;
    private LocalDateTime paymentAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Staff staff;
    private Services services;
    private Spares spares;

    public Order(
            Client client, Car car, Discount discount, Money price, PaymentType paymentType, LocalDateTime paymentAt) {
        this.client = client;
        this.car = car;
        this.discount = discount;
        this.price = price;
        this.paymentType = paymentType;
        this.paymentAt = paymentAt;
        this.staff = new StaffProxy();
        this.services = new ServicesProxy();
        this.spares = new SparesProxy();
    }

    public static OrderBuilderClient builder() {
        return client -> car -> discount -> price ->
                paymentType -> paymentAt -> () -> new Order(client, car, discount, price, paymentType, paymentAt);
    }

    @FunctionalInterface
    public interface OrderBuilderClient {
        OrderBuilderCar client(Client client);
    }

    @FunctionalInterface
    public interface OrderBuilderCar {
        OrderBuilderDiscount car(Car car);
    }

    @FunctionalInterface
    public interface OrderBuilderDiscount {
        OrderBuilderPrice discount(Discount discount);
    }

    @FunctionalInterface
    public interface OrderBuilderPrice {
        OrderBuilderPaymentType price(Money price);
    }

    @FunctionalInterface
    public interface OrderBuilderPaymentType {
        OrderBuilderPaymentAt paymentType(PaymentType paymentType);
    }

    @FunctionalInterface
    public interface OrderBuilderPaymentAt {
        OrderBuilder paymentAt(LocalDateTime paymentAt);
    }

    @FunctionalInterface
    public interface OrderBuilder {
        Order build();
    }

    public Client getClient() {
        return client;
    }

    public Car getCar() {
        return car;
    }

    public Discount getDiscount() {
        return discount;
    }

    public Money getPrice() {
        return price;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt;
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

    public Staff getProxyStaff() {
        return staff;
    }

    public List<Employee> getStaff() {
        return staff.get(id);
    }

    public void setProxyStaff(Staff staff) {
        this.staff = staff;
    }

    public Services getProxyServices() {
        return services;
    }

    public List<Service> getServices() {
        return services.get(id);
    }

    public void setProxyServices(Services services) {
        this.services = services;
    }

    public Spares getProxySpares() {
        return spares;
    }

    public List<Spare> getSpares() {
        return spares.get(id);
    }

    public void setProxySpares(Spares spares) {
        this.spares = spares;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
                .add("client=" + client)
                .add("car=" + car)
                .add("discount=" + discount)
                .add("price=" + price)
                .add("paymentType=" + paymentType)
                .add("paymentAt=" + paymentAt)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("staff=" + staff)
                .add("services=" + services)
                .add("spares=" + spares)
                .add("id=" + id)
                .toString();
    }

    public enum PaymentType {
        CARD("карта"),
        CASH("готівка");

        private String name;

        PaymentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
