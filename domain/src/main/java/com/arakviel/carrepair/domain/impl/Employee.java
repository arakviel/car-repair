package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import com.arakviel.carrepair.domain.proxy.Orders;
import com.arakviel.carrepair.domain.proxy.OrdersProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class Employee extends BaseDomain<UUID> implements Domain {

    private Address address;
    private Workroom workroom;
    private Position position;
    private String firstName;
    private String lastName;
    private String middleName;
    private byte[] photo;
    private byte[] passportDocCopy;
    private byte[] bankNumberDocCopy;
    private byte[] otherDocCopy;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private User user;
    private Orders orders;

    private Employee(
            Address address,
            Workroom workroom,
            Position position,
            String firstName,
            String lastName,
            byte[] photo,
            byte[] passportDocCopy) {
        this.address = address;
        this.workroom = workroom;
        this.position = position;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.passportDocCopy = passportDocCopy;
        this.orders = new OrdersProxy();
    }

    public static EmployeeBuilderAddress builder() {
        return address -> workroom -> position -> firstName -> lastName -> photo -> passportDocCopy ->
                () -> new Employee(address, workroom, position, firstName, lastName, photo, passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeBuilderAddress {
        EmployeeBuilderWorkroom address(Address address);
    }

    @FunctionalInterface
    public interface EmployeeBuilderWorkroom {
        EmployeeBuilderPosition workroom(Workroom workroom);
    }

    @FunctionalInterface
    public interface EmployeeBuilderPosition {
        EmployeeBuilderFirstName position(Position position);
    }

    @FunctionalInterface
    public interface EmployeeBuilderFirstName {
        EmployeeBuilderLastName firstName(String firstName);
    }

    @FunctionalInterface
    public interface EmployeeBuilderLastName {
        EmployeeBuilderPhoto lastName(String lastName);
    }

    @FunctionalInterface
    public interface EmployeeBuilderPhoto {
        EmployeePassportDocCopy photo(byte[] photo);
    }

    @FunctionalInterface
    public interface EmployeePassportDocCopy {
        EmployeeBuilder passportDocCopy(byte[] passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeBuilder {
        Employee build();
    }

    public Address getAddress() {
        return address;
    }

    public Workroom getWorkroom() {
        return workroom;
    }

    public Position getPosition() {
        return position;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public byte[] getPassportDocCopy() {
        return passportDocCopy;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public byte[] getBankNumberDocCopy() {
        return bankNumberDocCopy;
    }

    public void setBankNumberDocCopy(byte[] bankNumberDocCopy) {
        this.bankNumberDocCopy = bankNumberDocCopy;
    }

    public byte[] getOtherDocCopy() {
        return otherDocCopy;
    }

    public void setOtherDocCopy(byte[] otherDocCopy) {
        this.otherDocCopy = otherDocCopy;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Orders getProxyOrders() {
        return orders;
    }

    public List<Order> getOrders() {
        return orders.get(id);
    }

    public void setProxyOrders(Orders orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Employee.class.getSimpleName() + "[", "]")
                .add("address=" + address)
                .add("workroom=" + workroom)
                .add("position=" + position)
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("middleName='" + middleName + "'")
                .add("photo=" + photo)
                .add("passportDocCopy=" + passportDocCopy)
                .add("bankNumberDocCopy=" + bankNumberDocCopy)
                .add("otherDocCopy=" + otherDocCopy)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("user=" + user)
                .add("orders=" + orders)
                .add("id=" + id)
                .toString();
    }
}
