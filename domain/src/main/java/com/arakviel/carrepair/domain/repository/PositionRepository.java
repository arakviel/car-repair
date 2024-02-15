package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Position;
import java.util.List;

public interface PositionRepository extends GenericRepository<Integer, Position> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Position> getAllWhere(String name, Currency currency, Money salary);
}
