package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.ClientFilterDto;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientDao extends GenericDao<UUID, ClientEntity, ClientFilterDto> {

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<ClientEntity>
     */
    Optional<ClientEntity> findOneById(UUID id, Connection connection);

    List<ClientEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity);

    List<ClientEntity> findAllByWorkroomEntity(ClientFilterDto filter, WorkroomEntity workroomEntity);
}
