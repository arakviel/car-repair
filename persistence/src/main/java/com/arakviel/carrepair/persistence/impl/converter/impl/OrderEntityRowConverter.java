package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity.PaymentType;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderEntityRowConverter implements EntityRowConverter<OrderEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderEntityRowConverter.class);

    @Override
    public OrderEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            ClientEntity clientEntity = DaoFactory.getInstance()
                    .getClientDao()
                    .findOneById(UUID.fromString(resultSet.getString("client_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " ClientEntity, так як вона відсутня в"
                                                                   + " базі даних."));

            CarEntity carEntity = DaoFactory.getInstance()
                    .getCarDao()
                    .findOneById(UUID.fromString(resultSet.getString("car_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                            + " CarEntity, так як вона відсутня в"
                            + " базі даних."));

            DiscountEntity discountEntity = DaoFactory.getInstance()
                    .getDiscountDao()
                    .findOneById(resultSet.getInt("discount_id"), connection)
                    .orElse(null);

            OrderEntity orderEntity = OrderEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .clientEntity(clientEntity)
                    .carEntity(carEntity)
                    .discountEntity(discountEntity)
                    .price(new Money(resultSet.getInt("price_whole_part"), resultSet.getInt("price_decimal_part")))
                    .paymentType(PaymentType.valueOf(
                            resultSet.getString("payment_type").toUpperCase()))
                    .paymentAt(resultSet.getTimestamp("payment_at").toLocalDateTime())
                    .build();
            orderEntity.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
            orderEntity.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", orderEntity);
            return orderEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(OrderEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private OrderEntityRowConverter() {}

    private static class SingletonHolder {
        public static final OrderEntityRowConverter INSTANCE = new OrderEntityRowConverter();
    }

    public static OrderEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
