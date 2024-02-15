package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.impl.Service;
import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends GenericRepository<Integer, Service> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Service> getAllWhere(String name, Currency currency, Money price);

    List<Order> findAllOrders(Integer serviceId);

    void attachToOrder(Integer serviceId, UUID orderId, String description);

    void detachFromOrder(Integer serviceId, UUID orderId);
}
