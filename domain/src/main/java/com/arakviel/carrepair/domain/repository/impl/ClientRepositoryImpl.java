package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.ClientMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.repository.ClientRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.domain.validator.client.ClientValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.ClientDao;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.ClientFilterDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ClientRepositoryImpl implements ClientRepository {

    private final ClientDao clientDao;
    private final ClientMapper clientMapper;
    private final ClientValidatorChain clientValidatorChain;

    public ClientRepositoryImpl() {
        this.clientDao = DaoFactory.getInstance().getClientDao();
        this.clientMapper = MapperFactory.getInstance().getClientMapper();
        this.clientValidatorChain = ClientValidatorChain.getInstance();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Client get(UUID id) {
        var clientEntity = clientDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти клієнта по ідентифікатору"));
        return clientMapper.toDomain(clientEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Client> getAll() {
        List<ClientEntity> clientEntities = clientDao.findAll();
        return clientEntities.stream().map(clientMapper::toDomain).toList();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Entities
     */
    @Override
    public List<Client> getAllWhere(
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        var filter = new ClientFilterDto(phone, email, firstName, lastName, middleName, updatedAt, createdAt);
        List<ClientEntity> clientEntities = clientDao.findAll(filter);
        return clientEntities.stream().map(clientMapper::toDomain).toList();
    }

    @Override
    public List<Client> getAllByWorkroom(Workroom workroom) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        var orderEntities = clientDao.findAllByWorkroomEntity(workroomEntity);
        return orderEntities.stream().map(clientMapper::toDomain).toList();
    }

    @Override
    public List<Client> getAllByWorkroom() {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всіх клієнтів по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroom(curentWorkroom);
    }

    @Override
    public List<Client> getAllByWorkroomWhere(
            Workroom workroom,
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        var filter = new ClientFilterDto(phone, email, firstName, lastName, middleName, updatedAt, createdAt);
        List<ClientEntity> clientEntities = clientDao.findAllByWorkroomEntity(filter, workroomEntity);
        return clientEntities.stream().map(clientMapper::toDomain).toList();
    }

    @Override
    public List<Client> getAllByWorkroomWhere(
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всіх відфільтрованих клієнтів по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroomWhere(
                curentWorkroom, phone, email, firstName, lastName, middleName, updatedAt, createdAt);
    }

    /**
     * Save the entity to the database table.
     *
     * @param client persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Client add(Client client) {
        if (!clientValidatorChain.validate(client)) {
            throw new DomainAddException("Не вдалось додати клієнта із-за не валідних даних");
        }

        client.setId(null);
        LocalDateTime now = LocalDateTime.now();
        client.setUpdatedAt(now);
        client.setCreatedAt(now);
        var clientEntity = clientMapper.toEntity(client);
        clientEntity = clientDao.save(clientEntity);
        return clientMapper.toDomain(clientEntity);
    }

    /**
     * Update the client to the database table.
     *
     * @param id the identifier by which we want to update
     * @param client persistent entity
     */
    @Override
    public void set(UUID id, Client client) {
        if (!clientValidatorChain.validate(client)) {
            throw new DomainAddException("Не вдалось оновити клієнта із-за не валідних даних");
        }

        client.setId(id);
        client.setUpdatedAt(LocalDateTime.now());
        get(id);

        var clientEntity = clientMapper.toEntity(client);
        clientDao.save(clientEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        clientDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return clientValidatorChain.getValidationMessages();
    }
}
