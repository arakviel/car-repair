package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.util.List;
import java.util.UUID;

public interface SpareRepository extends GenericRepository<UUID, Spare> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Spare> getAllWhere(Workroom workroom, String name, Money price, Integer quantityInStock);

    List<Spare> getAllByWorkroom(Workroom workroom);

    List<Order> findAllOrders(UUID spareId);

    void attachToOrder(UUID spareId, UUID orderId, int quantity);

    void detachFromOrder(UUID spareId, UUID orderId);
}
