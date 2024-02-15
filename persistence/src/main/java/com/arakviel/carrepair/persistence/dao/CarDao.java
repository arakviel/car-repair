package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.CarFilterDto;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarDao extends GenericDao<UUID, CarEntity, CarFilterDto> {

    /**
     * Get an entity object by identifier with connection.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<CarEntity>
     */
    Optional<CarEntity> findOneById(UUID id, Connection connection);

    List<CarEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity);

    List<CarEntity> findAllByWorkroomEntity(CarFilterDto filter, WorkroomEntity workroomEntity);
}
