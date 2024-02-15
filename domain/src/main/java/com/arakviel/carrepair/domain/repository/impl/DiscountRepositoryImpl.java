package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.mapper.DiscountMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.repository.DiscountRepository;
import com.arakviel.carrepair.domain.validator.discount.DiscountValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.DiscountDao;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.filter.impl.DiscountFilterDto;
import java.util.List;
import java.util.Map;

public final class DiscountRepositoryImpl implements DiscountRepository {

    private final DiscountDao discountDao;
    private final DiscountMapper discountMapper;
    private final DiscountValidatorChain discountValidatorChain;

    public DiscountRepositoryImpl() {
        discountDao = DaoFactory.getInstance().getDiscountDao();
        discountMapper = MapperFactory.getInstance().getDiscountMapper();
        discountValidatorChain = DiscountValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Discount> getAllWhere(String name, Short value) {
        var filter = new DiscountFilterDto(name, value);
        List<DiscountEntity> discountEntities = discountDao.findAll(filter);
        return discountEntities.stream().map(discountMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Discount get(Integer id) {
        DiscountEntity discountEntity = discountDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти акцію по ідентифікатору"));
        return discountMapper.toDomain(discountEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Discount> getAll() {
        List<DiscountEntity> discountEntities = discountDao.findAll();
        return discountEntities.stream().map(discountMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param discount persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Discount add(Discount discount) {
        if (!discountValidatorChain.validate(discount)) {
            throw new DomainAddException("Не вдалось додати акцію із-за не валідних даних");
        }
        discount.setId(null);
        var discountEntity = discountMapper.toEntity(discount);
        discountEntity = discountDao.save(discountEntity);
        return discountMapper.toDomain(discountEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param discount persistent entity
     */
    @Override
    public void set(Integer id, Discount discount) {
        if (!discountValidatorChain.validate(discount)) {
            throw new DomainAddException("Не вдалось оновити акцію із-за не валідних даних");
        }
        discount.setId(id);
        get(id);

        var discountEntity = discountMapper.toEntity(discount);
        discountDao.save(discountEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        get(id);
        discountDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return discountValidatorChain.getValidationMessages();
    }
}
