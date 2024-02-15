package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import com.arakviel.carrepair.presentation.model.proxy.EmployeeModels;
import com.arakviel.carrepair.presentation.model.proxy.EmployeeModelsProxy;
import com.arakviel.carrepair.presentation.model.proxy.ServiceModels;
import com.arakviel.carrepair.presentation.model.proxy.ServiceModelsProxy;
import com.arakviel.carrepair.presentation.model.proxy.SpareModels;
import com.arakviel.carrepair.presentation.model.proxy.SpareModelsProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OrderModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<ClientModel> clientModel = new SimpleObjectProperty<>();
    private ObjectProperty<CarModel> carModel = new SimpleObjectProperty<>();
    private ObjectProperty<DiscountModel> discountModel = new SimpleObjectProperty<>();
    private ObjectProperty<MoneyModel> price = new SimpleObjectProperty<>();
    private ObjectProperty<PaymentType> paymentType = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> paymentAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private EmployeeModels employeeModels;
    private ServiceModels serviceModels;
    private SpareModels spareModels;

    public OrderModel(
            ClientModel clientModel,
            CarModel carModel,
            DiscountModel discountModel,
            MoneyModel price,
            PaymentType paymentType,
            LocalDateTime paymentAt) {
        this.id = new SimpleObjectProperty<>();
        this.clientModel.set(clientModel);
        this.carModel.set(carModel);
        this.discountModel.set(discountModel);
        this.price.set(price);
        this.paymentType.set(paymentType);
        this.paymentAt.set(paymentAt);
        this.employeeModels = new EmployeeModelsProxy();
        this.serviceModels = new ServiceModelsProxy();
        this.spareModels = new SpareModelsProxy();
    }

    public static OrderModelBuilderClient builder() {
        return clientModel -> carModel -> discountModel -> price -> paymentType ->
                paymentAt -> () -> new OrderModel(clientModel, carModel, discountModel, price, paymentType, paymentAt);
    }

    @FunctionalInterface
    public interface OrderModelBuilderClient {
        OrderModelBuilderCar clientModel(ClientModel clientModel);
    }

    @FunctionalInterface
    public interface OrderModelBuilderCar {
        OrderModelBuilderDiscount carModel(CarModel carModel);
    }

    @FunctionalInterface
    public interface OrderModelBuilderDiscount {
        OrderModelBuilderPrice discountModel(DiscountModel discountModel);
    }

    @FunctionalInterface
    public interface OrderModelBuilderPrice {
        OrderModelBuilderPaymentType price(MoneyModel price);
    }

    @FunctionalInterface
    public interface OrderModelBuilderPaymentType {
        OrderModelBuilderPaymentAt paymentType(PaymentType paymentType);
    }

    @FunctionalInterface
    public interface OrderModelBuilderPaymentAt {
        OrderModelBuilder paymentAt(LocalDateTime paymentAt);
    }

    @FunctionalInterface
    public interface OrderModelBuilder {
        OrderModel build();
    }

    public ClientModel getClientModel() {
        return clientModel.get();
    }

    public ObjectProperty<ClientModel> clientModelProperty() {
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel.set(clientModel);
    }

    public CarModel getCarModel() {
        return carModel.get();
    }

    public ObjectProperty<CarModel> carModelProperty() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel.set(carModel);
    }

    public DiscountModel getDiscountModel() {
        return discountModel.get();
    }

    public ObjectProperty<DiscountModel> discountModelProperty() {
        return discountModel;
    }

    public void setDiscountModel(DiscountModel discountModel) {
        this.discountModel.set(discountModel);
    }

    public MoneyModel getPrice() {
        return price.get();
    }

    public ObjectProperty<MoneyModel> priceProperty() {
        return price;
    }

    public void setPrice(MoneyModel price) {
        this.price.set(price);
    }

    public PaymentType getPaymentType() {
        return paymentType.get();
    }

    public ObjectProperty<PaymentType> paymentTypeProperty() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType.set(paymentType);
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt.get();
    }

    public ObjectProperty<LocalDateTime> paymentAtProperty() {
        return paymentAt;
    }

    public void setPaymentAt(LocalDateTime paymentAt) {
        this.paymentAt.set(paymentAt);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public EmployeeModels getEmployeeModelsProxy() {
        return employeeModels;
    }

    public List<EmployeeModel> getEmployeeModels() {
        return employeeModels.get(id.get());
    }

    public void setEmployeeModelsProxy(EmployeeModels employeeModelsProxy) {
        this.employeeModels = employeeModelsProxy;
    }

    public ServiceModels getServiceModelsProxy() {
        return serviceModels;
    }

    public List<ServiceModel> getServiceModels() {
        return serviceModels.get(id.get());
    }

    public void setServiceModels(ServiceModels serviceModelsProxy) {
        this.serviceModels = serviceModelsProxy;
    }

    public SpareModels getSpareModelsProxy() {
        return spareModels;
    }

    public List<SpareModel> getSpareModels() {
        return spareModels.get(id.get());
    }

    public void setSpareModels(SpareModels spareModelsProxy) {
        this.spareModels = spareModelsProxy;
    }

    @Override
    public OrderModel clone() {
        try {
            OrderModel cloned = (OrderModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.clientModel = new SimpleObjectProperty<>(this.clientModel.get());
            cloned.carModel = new SimpleObjectProperty<>(this.carModel.get());
            cloned.discountModel = new SimpleObjectProperty<>(this.discountModel.get());
            cloned.price = new SimpleObjectProperty<>(this.price.get());
            cloned.paymentType = new SimpleObjectProperty<>(this.paymentType.get());
            cloned.paymentAt = new SimpleObjectProperty<>(this.paymentAt.get());
            cloned.updatedAt = new SimpleObjectProperty<>(this.updatedAt.get());
            cloned.createdAt = new SimpleObjectProperty<>(this.createdAt.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
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

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderModel.class.getSimpleName() + "[", "]")
                .add("clientModel=" + clientModel)
                .add("carModel=" + carModel)
                .add("discountModel=" + discountModel)
                .add("price=" + price)
                .add("paymentType=" + paymentType)
                .add("paymentAt=" + paymentAt)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("employeeModels=" + employeeModels)
                .add("serviceModels=" + serviceModels)
                .add("spareModels=" + spareModels)
                .add("id=" + id)
                .toString();
    }
}
