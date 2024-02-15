package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.mapper.CurrencyMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.PositionMapper;
import com.arakviel.carrepair.domain.repository.PositionRepository;
import com.arakviel.carrepair.domain.validator.position.PositionValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.PositionDao;
import com.arakviel.carrepair.persistence.entity.impl.PositionEntity;
import com.arakviel.carrepair.persistence.filter.impl.PositionFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PositionRepositoryImpl implements PositionRepository {

    private final PositionDao positionDao;
    private final PositionMapper positionMapper;
    private final PositionValidatorChain positionValidatorChain;

    public PositionRepositoryImpl() {
        positionDao = DaoFactory.getInstance().getPositionDao();
        positionMapper = MapperFactory.getInstance().getPositionMapper();
        positionValidatorChain = PositionValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Position> getAllWhere(String name, Currency currency, Money salary) {
        CurrencyMapper currencyMapper = MapperFactory.getInstance().getCurrencyMapper();
        var filter = new PositionFilterDto(
                name,
                Objects.nonNull(currency) ? currencyMapper.toEntity(currency) : null,
                Objects.nonNull(salary)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                salary.wholePart(), salary.decimalPart())
                        : null);
        List<PositionEntity> positionEntities = positionDao.findAll(filter);
        return positionEntities.stream().map(positionMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Position get(Integer id) {
        PositionEntity positionEntity = positionDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти професію по ідентифікатору"));
        return positionMapper.toDomain(positionEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Position> getAll() {
        List<PositionEntity> positionEntities = positionDao.findAll();
        return positionEntities.stream().map(positionMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param position persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Position add(Position position) {
        if (!positionValidatorChain.validate(position)) {
            throw new DomainAddException("Не вдалось додати професію із-за не валідних даних");
        }
        position.setId(null);
        var positionEntity = positionMapper.toEntity(position);
        positionEntity = positionDao.save(positionEntity);
        return positionMapper.toDomain(positionEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param position persistent entity
     */
    @Override
    public void set(Integer id, Position position) {
        if (!positionValidatorChain.validate(position)) {
            throw new DomainAddException("Не вдалось оновити професію із-за не валідних даних");
        }
        position.setId(id);
        get(id);

        var positionEntity = positionMapper.toEntity(position);
        positionDao.save(positionEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        get(id);
        positionDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return positionValidatorChain.getValidationMessages();
    }
}
