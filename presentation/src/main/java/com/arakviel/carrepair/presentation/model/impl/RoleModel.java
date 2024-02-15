package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.StringJoiner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RoleModel extends BaseModel<Integer> implements Model, Cloneable {

    private BooleanProperty canEditUsers = new SimpleBooleanProperty();
    private BooleanProperty canEditSpares = new SimpleBooleanProperty();
    private BooleanProperty canEditClients = new SimpleBooleanProperty();
    private BooleanProperty canEditServices = new SimpleBooleanProperty();
    private BooleanProperty canEditOrders = new SimpleBooleanProperty();
    private BooleanProperty canEditPayrolls = new SimpleBooleanProperty();
    private ObjectProperty<PositionModel> positionModel = new SimpleObjectProperty<>();

    public RoleModel(
            Integer id,
            boolean canEditUsers,
            boolean canEditSpares,
            boolean canEditClients,
            boolean canEditServices,
            boolean canEditOrders,
            boolean canEditPayrolls) {
        this.id = new SimpleObjectProperty<>(id);
        this.canEditUsers.set(canEditUsers);
        this.canEditSpares.set(canEditSpares);
        this.canEditClients.set(canEditClients);
        this.canEditServices.set(canEditServices);
        this.canEditOrders.set(canEditOrders);
        this.canEditPayrolls.set(canEditPayrolls);
    }

    public static RoleModelBuilderId builder() {
        return id -> canEditUsers -> canEditSpares ->
                canEditClients -> canEditServices -> canEditOrders -> canEditPayrolls -> () -> new RoleModel(
                        id,
                        canEditUsers,
                        canEditSpares,
                        canEditClients,
                        canEditServices,
                        canEditOrders,
                        canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleModelBuilderId {
        RoleModelBuilderCanEditUsers id(Integer id);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditUsers {
        RoleModelBuilderCanEditSpares canEditUsers(boolean canEditUsers);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditSpares {
        RoleModelBuilderCanEditClients canEditSpares(boolean canEditSpares);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditClients {
        RoleModelBuilderCanEditServices canEditClients(boolean canEditClients);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditServices {
        RoleModelBuilderCanEditOrders canEditServices(boolean canEditServices);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditOrders {
        RoleModelBuilderCanEditPayrolls canEditOrders(boolean canEditOrders);
    }

    @FunctionalInterface
    public interface RoleModelBuilderCanEditPayrolls {
        RoleModelBuilder canEditPayrolls(boolean canEditPayrolls);
    }

    @FunctionalInterface
    public interface RoleModelBuilder {
        RoleModel build();
    }

    public boolean canEditUsers() {
        return canEditUsers.get();
    }

    public BooleanProperty canEditUsersProperty() {
        return canEditUsers;
    }

    public void setCanEditUsers(boolean canEditUsers) {
        this.canEditUsers.set(canEditUsers);
    }

    public boolean canEditSpares() {
        return canEditSpares.get();
    }

    public BooleanProperty canEditSparesProperty() {
        return canEditSpares;
    }

    public void setCanEditSpares(boolean canEditSpares) {
        this.canEditSpares.set(canEditSpares);
    }

    public boolean canEditClients() {
        return canEditClients.get();
    }

    public BooleanProperty canEditClientsProperty() {
        return canEditClients;
    }

    public void setCanEditClients(boolean canEditClients) {
        this.canEditClients.set(canEditClients);
    }

    public boolean canEditServices() {
        return canEditServices.get();
    }

    public BooleanProperty canEditServicesProperty() {
        return canEditServices;
    }

    public void setCanEditServices(boolean canEditServices) {
        this.canEditServices.set(canEditServices);
    }

    public boolean canEditOrders() {
        return canEditOrders.get();
    }

    public BooleanProperty canEditOrdersProperty() {
        return canEditOrders;
    }

    public void setCanEditOrders(boolean canEditOrders) {
        this.canEditOrders.set(canEditOrders);
    }

    public boolean canEditPayrolls() {
        return canEditPayrolls.get();
    }

    public BooleanProperty canEditPayrollsProperty() {
        return canEditPayrolls;
    }

    public void setCanEditPayrolls(boolean canEditPayrolls) {
        this.canEditPayrolls.set(canEditPayrolls);
    }

    public PositionModel getPositionModel() {
        return positionModel.get();
    }

    public ObjectProperty<PositionModel> positionModelProperty() {
        return positionModel;
    }

    public void setPositionModel(PositionModel positionModel) {
        this.positionModel.set(positionModel);
    }

    @Override
    public RoleModel clone() {
        try {
            RoleModel cloned = (RoleModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.canEditUsers = new SimpleBooleanProperty(this.canEditUsers.get());
            cloned.canEditSpares = new SimpleBooleanProperty(this.canEditSpares.get());
            cloned.canEditClients = new SimpleBooleanProperty(this.canEditClients.get());
            cloned.canEditServices = new SimpleBooleanProperty(this.canEditServices.get());
            cloned.canEditOrders = new SimpleBooleanProperty(this.canEditOrders.get());
            cloned.canEditPayrolls = new SimpleBooleanProperty(this.canEditPayrolls.get());
            cloned.positionModel = new SimpleObjectProperty<>(this.positionModel.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RoleModel.class.getSimpleName() + "[", "]")
                .add("canEditUsers=" + canEditUsers)
                .add("canEditSpares=" + canEditSpares)
                .add("canEditClients=" + canEditClients)
                .add("canEditServices=" + canEditServices)
                .add("canEditOrders=" + canEditOrders)
                .add("canEditPayrolls=" + canEditPayrolls)
                .add("positionModel=" + positionModel)
                .add("id=" + id)
                .toString();
    }
}
