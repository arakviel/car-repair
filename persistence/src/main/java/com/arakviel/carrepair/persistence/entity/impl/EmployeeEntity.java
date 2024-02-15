package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import com.arakviel.carrepair.persistence.entity.proxy.OrderEntities;
import com.arakviel.carrepair.persistence.entity.proxy.OrderEntitiesProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class EmployeeEntity extends BaseEntity<UUID> implements Entity {

    private AddressEntity addressEntity;
    private WorkroomEntity workroomEntity;
    private PositionEntity positionEntity;
    private String firstName;
    private String lastName;
    private String middleName;
    private byte[] photo;
    private byte[] passportDocCopy;
    private byte[] bankNumberDocCopy;
    private byte[] otherDocCopy;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private UserEntity userEntity;
    private OrderEntities orderEntities;

    private EmployeeEntity(
            UUID id,
            AddressEntity addressEntity,
            WorkroomEntity workroomEntity,
            PositionEntity positionEntity,
            String firstName,
            String lastName,
            byte[] photo,
            byte[] passportDocCopy) {
        super(id);
        this.addressEntity = addressEntity;
        this.workroomEntity = workroomEntity;
        this.positionEntity = positionEntity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.passportDocCopy = passportDocCopy;
        this.orderEntities = new OrderEntitiesProxy();
    }

    public static EmployeeEntityBuilderId builder() {
        return id -> addressEntity -> workroomEntity ->
                positionEntity -> firstName -> lastName -> photo -> passportDocCopy -> () -> new EmployeeEntity(
                        id, addressEntity, workroomEntity, positionEntity, firstName, lastName, photo, passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderId {
        EmployeeEntityBuilderAddressEntity id(UUID id);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderAddressEntity {
        EmployeeEntityBuilderWorkroomEntity addressEntity(AddressEntity addressEntity);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderWorkroomEntity {
        EmployeeEntityBuilderPositionEntity workroomEntity(WorkroomEntity workroomEntity);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderPositionEntity {
        EmployeeEntityBuilderFirstName positionEntity(PositionEntity positionEntity);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderFirstName {
        EmployeeEntityBuilderLastName firstName(String firstName);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderLastName {
        EmployeeEntityBuilderPhoto lastName(String lastName);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilderPhoto {
        EmployeeEntityPassportDocCopy photo(byte[] photo);
    }

    @FunctionalInterface
    public interface EmployeeEntityPassportDocCopy {
        EmployeeEntityBuilder passportDocCopy(byte[] passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeEntityBuilder {
        EmployeeEntity build();
    }

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public WorkroomEntity getWorkroomEntity() {
        return workroomEntity;
    }

    public PositionEntity getPositionEntity() {
        return positionEntity;
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

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    /*    public UserEntity getUserEntity() {
        if (Objects.isNull(userEntity)) {
            userEntity = DaoFactory.getInstance()
                    .getUserDao()
                    .findOneById(id)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Напевно, робітник ще не має облікового" + " запису"));
        }
        return userEntity;
    }*/

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public OrderEntities getProxyOrderEntities() {
        return orderEntities;
    }

    public List<OrderEntity> getOrderEntities() {
        return orderEntities.get(id);
    }

    public void setOrderEntities(OrderEntities orderEntities) {
        this.orderEntities = orderEntities;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EmployeeEntity.class.getSimpleName() + "[", "]")
                .add("addressEntity=" + addressEntity)
                .add("workroomEntity=" + workroomEntity)
                .add("positionEntity=" + positionEntity)
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("middleName='" + middleName + "'")
                .add("photo=" + photo)
                .add("passportDocCopy=" + passportDocCopy)
                .add("bankNumberDocCopy=" + bankNumberDocCopy)
                .add("otherDocCopy=" + otherDocCopy)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("userEntity=" + userEntity)
                .add("orders=" + orderEntities)
                .add("id=" + id)
                .toString();
    }
}
