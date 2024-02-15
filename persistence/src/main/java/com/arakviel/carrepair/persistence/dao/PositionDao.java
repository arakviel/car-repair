package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.PositionEntity;
import com.arakviel.carrepair.persistence.filter.impl.PositionFilterDto;
import java.sql.Connection;
import java.util.Optional;

public interface PositionDao extends GenericDao<Integer, PositionEntity, PositionFilterDto> {

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<PositionEntity>
     */
    Optional<PositionEntity> findOneById(Integer id, Connection connection);
}
