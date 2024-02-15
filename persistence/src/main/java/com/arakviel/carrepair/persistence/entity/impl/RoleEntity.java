package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;

public class RoleEntity extends BaseEntity<Integer> implements Entity {

    private boolean canEditUsers;
    private boolean canEditSpares;
    private boolean canEditClients;
    private boolean canEditServices;
    private boolean canEditOrders;
    private boolean canEditPayrolls;
    private PositionEntity positionEntity;

    private RoleEntity(
            Integer id,
            boolean canEditUsers,
            boolean canEditSpares,
            boolean canEditClients,
            boolean canEditServices,
            boolean canEditOrders,
            boolean canEditPayrolls) {
        super(id);
        this.canEditUsers = canEditUsers;
        this.canEditSpares = canEditSpares;
        this.canEditClients = canEditClients;
        this.canEditServices = canEditServices;
        this.canEditOrders = canEditOrders;
        this.canEditPayrolls = canEditPayrolls;
    }

    public static RoleEntityBuilderId builder() {
        return id -> canEditUsers -> canEditSpares ->
                canEditClients -> canEditServices -> canEditOrders -> canEditPayrolls -> () -> new RoleEntity(
                        id,
                        canEditUsers,
                        canEditSpares,
                        canEditClients,
                        canEditServices,
                        canEditOrders,
                        canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderId {
        RoleEntityBuilderCanEditUsers id(Integer id);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditUsers {
        RoleEntityBuilderCanEditSpares canEditUsers(boolean canEditUsers);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditSpares {
        RoleEntityBuilderCanEditClients canEditSpares(boolean canEditSpares);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditClients {
        RoleEntityBuilderCanEditServices canEditClients(boolean canEditClients);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditServices {
        RoleEntityBuilderCanEditOrders canEditServices(boolean canEditServices);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditOrders {
        RoleEntityBuilderCanEditPayrolls canEditOrders(boolean canEditOrders);
    }

    @FunctionalInterface
    public interface RoleEntityBuilderCanEditPayrolls {
        RoleEntityBuilder canEditPayrolls(boolean canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleEntityBuilder {
        RoleEntity build();
    }

    public boolean canEditUsers() {
        return canEditUsers;
    }

    public boolean canEditSpares() {
        return canEditSpares;
    }

    public boolean canEditClients() {
        return canEditClients;
    }

    public boolean canEditServices() {
        return canEditServices;
    }

    public boolean canEditOrders() {
        return canEditOrders;
    }

    public boolean canEditPayrolls() {
        return canEditPayrolls;
    }

    public void setPositionEntity(PositionEntity positionEntity) {
        this.positionEntity = positionEntity;
    }

    /*    public PositionEntity getPositionEntity() {
        if (Objects.isNull(positionEntity)) {
            positionEntity = DaoFactory.getInstance()
                    .getPositionDao()
                    .findOneById(id)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Дивовижно, але цієї помилки не може існувати" + " О.о"));
        }
        return positionEntity;
    }*/

    public PositionEntity getPositionEntity() {
        return positionEntity;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RoleEntity.class.getSimpleName() + "[", "]")
                .add("canEditUsers=" + canEditUsers)
                .add("canEditSpares=" + canEditSpares)
                .add("canEditClients=" + canEditClients)
                .add("canEditServices=" + canEditServices)
                .add("canEditOrders=" + canEditOrders)
                .add("canEditPayrolls=" + canEditPayrolls)
                .add("positionEntity=" + positionEntity)
                .add("id=" + id)
                .toString();
    }
}
