package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import com.arakviel.carrepair.persistence.entity.proxy.EmployeeEntities;
import com.arakviel.carrepair.persistence.entity.proxy.EmployeeEntitiesProxy;
import com.arakviel.carrepair.persistence.entity.proxy.ServiceEntities;
import com.arakviel.carrepair.persistence.entity.proxy.ServiceEntitiesProxy;
import com.arakviel.carrepair.persistence.entity.proxy.SpareEntities;
import com.arakviel.carrepair.persistence.entity.proxy.SpareEntitiesProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class OrderEntity extends BaseEntity<UUID> implements Entity {

    private ClientEntity clientEntity;
    private CarEntity carEntity;
    private DiscountEntity discountEntity;
    private Money price;
    private PaymentType paymentType;
    private LocalDateTime paymentAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private EmployeeEntities employeeEntities;
    private ServiceEntities serviceEntities;
    private SpareEntities spareEntities;

    private OrderEntity(
            UUID id,
            ClientEntity clientEntity,
            CarEntity carEntity,
            DiscountEntity discountEntity,
            Money price,
            PaymentType paymentType,
            LocalDateTime paymentAt) {
        super(id);
        this.clientEntity = clientEntity;
        this.carEntity = carEntity;
        this.discountEntity = discountEntity;
        this.price = price;
        this.paymentType = paymentType;
        this.paymentAt = paymentAt;
        this.employeeEntities = new EmployeeEntitiesProxy();
        this.serviceEntities = new ServiceEntitiesProxy();
        this.spareEntities = new SpareEntitiesProxy();
    }

    public static OrderEntityBuilderId builder() {
        return id -> clientEntity -> carEntity -> discountEntity -> price -> paymentType -> paymentAt ->
                () -> new OrderEntity(id, clientEntity, carEntity, discountEntity, price, paymentType, paymentAt);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderId {
        OrderEntityBuilderClientEntity id(UUID id);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderClientEntity {
        OrderEntityBuilderCarEntity clientEntity(ClientEntity clientEntity);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderCarEntity {
        OrderEntityBuilderDiscountEntity carEntity(CarEntity carEntity);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderDiscountEntity {
        OrderEntityBuilderPrice discountEntity(DiscountEntity discountEntity);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderPrice {
        OrderEntityBuilderPaymentType price(Money price);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderPaymentType {
        OrderEntityBuilderPaymentAt paymentType(PaymentType paymentType);
    }

    @FunctionalInterface
    public interface OrderEntityBuilderPaymentAt {
        OrderEntityBuilder paymentAt(LocalDateTime paymentAt);
    }

    @FunctionalInterface
    public interface OrderEntityBuilder {
        OrderEntity build();
    }

    public ClientEntity getClientEntity() {
        return clientEntity;
    }

    public CarEntity getCarEntity() {
        return carEntity;
    }

    public DiscountEntity getDiscountEntity() {
        return discountEntity;
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

    public void setStaff(EmployeeEntities employeeEntities) {
        this.employeeEntities = employeeEntities;
    }

    public EmployeeEntities getProxyStaff() {
        return employeeEntities;
    }

    public List<EmployeeEntity> getStaff() {
        return employeeEntities.get(id);
    }

    public ServiceEntities getProxyServices() {
        return serviceEntities;
    }

    public List<ServiceEntity> getServices() {
        return serviceEntities.get(id);
    }

    public void setServices(ServiceEntities serviceEntities) {
        this.serviceEntities = serviceEntities;
    }

    public List<SpareEntity> getSpares() {
        return spareEntities.get(id);
    }

    public void setSpares(SpareEntities spareEntities) {
        this.spareEntities = spareEntities;
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
        return new StringJoiner(", ", OrderEntity.class.getSimpleName() + "[", "]")
                .add("clientEntity=" + clientEntity)
                .add("carEntity=" + carEntity)
                .add("discountEntity=" + discountEntity)
                .add("price=" + price)
                .add("paymentType=" + paymentType)
                .add("paymentAt=" + paymentAt)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("staff=" + employeeEntities)
                .add("services=" + serviceEntities)
                .add("spares=" + spareEntities)
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
