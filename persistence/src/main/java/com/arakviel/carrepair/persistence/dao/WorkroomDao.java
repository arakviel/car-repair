package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.WorkroomFilterDto;
import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;

public interface WorkroomDao extends GenericDao<UUID, WorkroomEntity, WorkroomFilterDto> {

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<WorkroomEntity>
     */
    Optional<WorkroomEntity> findOneById(UUID id, Connection connection);
}
