package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.DiscountModel;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.OrderModel;
import java.util.Objects;

public class OrderModelMapper implements ModelMapper<Order, OrderModel> {

    @Override
    public OrderModel toModel(Order order) {
        var modelMapperFactory = ModelMapperFactory.getInstance();
        var clientModelMapper = modelMapperFactory.getClientModelMapper();
        var carModelMapper = modelMapperFactory.getCarModelMapper();
        var discountModelMapper = modelMapperFactory.getDiscountModelMapper();
        DiscountModel discountModel = null;
        Discount discount = order.getDiscount();
        if (Objects.nonNull(discount)) {
            discountModel = discountModelMapper.toModel(discount);
        }

        var orderModel = OrderModel.builder()
                .clientModel(clientModelMapper.toModel(order.getClient()))
                .carModel(carModelMapper.toModel(order.getCar()))
                .discountModel(discountModel)
                .price(new MoneyModel(
                        order.getPrice().wholePart(), order.getPrice().decimalPart()))
                .paymentType(
                        OrderModel.PaymentType.valueOf(order.getPaymentType().toString()))
                .paymentAt(order.getPaymentAt())
                .build();
        orderModel.setId(order.getId());
        orderModel.setUpdatedAt(order.getUpdatedAt());
        orderModel.setCreatedAt(order.getCreatedAt());
        return orderModel;
    }

    @Override
    public Order toDomain(OrderModel orderModel) {
        var modelMapperFactory = ModelMapperFactory.getInstance();
        var clientModelMapper = modelMapperFactory.getClientModelMapper();
        var carModelMapper = modelMapperFactory.getCarModelMapper();
        var discountModelMapper = modelMapperFactory.getDiscountModelMapper();

        Discount discount = null;
        DiscountModel discountModel = orderModel.getDiscountModel();
        if (Objects.nonNull(discountModel)) {
            discount = discountModelMapper.toDomain(discountModel);
        }

        var order = Order.builder()
                .client(clientModelMapper.toDomain(orderModel.getClientModel()))
                .car(carModelMapper.toDomain(orderModel.getCarModel()))
                .discount(discount)
                .price(new Money(
                        orderModel.getPrice().wholePart(), orderModel.getPrice().decimalPart()))
                .paymentType(
                        Order.PaymentType.valueOf(orderModel.getPaymentType().toString()))
                .paymentAt(orderModel.getPaymentAt())
                .build();
        order.setId(orderModel.getId());
        order.setUpdatedAt(orderModel.getUpdatedAt());
        order.setCreatedAt(orderModel.getCreatedAt());
        return order;
    }
}
