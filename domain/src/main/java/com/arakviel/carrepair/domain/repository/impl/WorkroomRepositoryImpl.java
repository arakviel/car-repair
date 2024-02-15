package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.WorkroomMapper;
import com.arakviel.carrepair.domain.repository.WorkroomRepository;
import com.arakviel.carrepair.domain.validator.workroom.WorkroomValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.WorkroomDao;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.WorkroomFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class WorkroomRepositoryImpl implements WorkroomRepository {
    private final WorkroomDao workroomDao;
    private final WorkroomMapper workroomMapper;
    private final WorkroomValidatorChain workroomValidatorChain;

    public WorkroomRepositoryImpl() {
        workroomDao = DaoFactory.getInstance().getWorkroomDao();
        workroomMapper = MapperFactory.getInstance().getWorkroomMapper();
        workroomValidatorChain = WorkroomValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Workroom> getAllWhere(Address address, String name) {
        var addressMapper = MapperFactory.getInstance().getAddressMapper();
        var filter = new WorkroomFilterDto(Objects.nonNull(address) ? addressMapper.toEntity(address) : null, name);
        List<WorkroomEntity> workroomEntities = workroomDao.findAll(filter);
        return workroomEntities.stream().map(workroomMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Workroom get(UUID id) {
        WorkroomEntity workroomEntity = workroomDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти відділ по ідентифікатору"));
        return workroomMapper.toDomain(workroomEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Workroom> getAll() {
        List<WorkroomEntity> workroomEntities = workroomDao.findAll();
        return workroomEntities.stream().map(workroomMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param workroom persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Workroom add(Workroom workroom) {
        if (!workroomValidatorChain.validate(workroom)) {
            throw new DomainAddException("Не вдалось додати відділ із-за не валідних даних");
        }
        workroom.setId(null);
        var workroomEntity = workroomMapper.toEntity(workroom);
        workroomEntity = workroomDao.save(workroomEntity);
        return workroomMapper.toDomain(workroomEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param workroom persistent entity
     */
    @Override
    public void set(UUID id, Workroom workroom) {
        if (!workroomValidatorChain.validate(workroom)) {
            throw new DomainAddException("Не вдалось оновити відділ із-за не валідних даних");
        }
        workroom.setId(id);
        get(id);

        var workroomEntity = workroomMapper.toEntity(workroom);
        workroomDao.save(workroomEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        workroomDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return workroomValidatorChain.getValidationMessages();
    }
}
