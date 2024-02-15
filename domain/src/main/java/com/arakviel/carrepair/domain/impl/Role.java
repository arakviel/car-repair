package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Role extends BaseDomain<Integer> implements Domain {

    private boolean canEditUsers;
    private boolean canEditSpares;
    private boolean canEditClients;
    private boolean canEditServices;
    private boolean canEditOrders;
    private boolean canEditPayrolls;
    private Position position;

    public Role(
            Integer id,
            boolean canEditUsers,
            boolean canEditSpares,
            boolean canEditClients,
            boolean canEditServices,
            boolean canEditOrders,
            boolean canEditPayrolls) {
        super.id = id;
        this.canEditUsers = canEditUsers;
        this.canEditSpares = canEditSpares;
        this.canEditClients = canEditClients;
        this.canEditServices = canEditServices;
        this.canEditOrders = canEditOrders;
        this.canEditPayrolls = canEditPayrolls;
    }

    public static RoleBuilderId builder() {
        return id -> canEditUsers -> canEditSpares ->
                canEditClients -> canEditServices -> canEditOrders -> canEditPayrolls -> () -> new Role(
                        id,
                        canEditUsers,
                        canEditSpares,
                        canEditClients,
                        canEditServices,
                        canEditOrders,
                        canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleBuilderId {
        RoleBuilderCanEditUsers id(Integer id);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditUsers {
        RoleBuilderCanEditSpares canEditUsers(boolean canEditUsers);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditSpares {
        RoleBuilderCanEditClients canEditSpares(boolean canEditSpares);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditClients {
        RoleBuilderCanEditServices canEditClients(boolean canEditClients);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditServices {
        RoleBuilderCanEditOrders canEditServices(boolean canEditServices);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditOrders {
        RoleBuilderCanEditPayrolls canEditOrders(boolean canEditOrders);
    }

    @FunctionalInterface
    public interface RoleBuilderCanEditPayrolls {
        RoleBuilder canEditPayrolls(boolean canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleBuilder {
        Role build();
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Role.class.getSimpleName() + "[", "]")
                .add("canEditUsers=" + canEditUsers)
                .add("canEditSpares=" + canEditSpares)
                .add("canEditClients=" + canEditClients)
                .add("canEditServices=" + canEditServices)
                .add("canEditOrders=" + canEditOrders)
                .add("canEditPayrolls=" + canEditPayrolls)
                .add("position=" + position)
                .add("id=" + id)
                .toString();
    }
}
