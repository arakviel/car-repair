package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.AddressFilterDto;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressDao extends GenericDao<UUID, AddressEntity, AddressFilterDto> {

    /**
     * Get an entity object by identifier with connection.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<AddressEntity>
     */
    Optional<AddressEntity> findOneById(UUID id, Connection connection);

    List<AddressEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity);

    List<AddressEntity> findAllByWorkroomEntity(AddressFilterDto filter, WorkroomEntity workroomEntity);
}
