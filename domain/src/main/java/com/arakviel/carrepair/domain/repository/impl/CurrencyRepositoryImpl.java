package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.mapper.CurrencyMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.repository.CurrencyRepository;
import com.arakviel.carrepair.domain.validator.currency.CurrencyValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.CurrencyDao;
import com.arakviel.carrepair.persistence.entity.impl.CurrencyEntity;
import com.arakviel.carrepair.persistence.filter.impl.CurrencyFilterDto;
import java.util.List;
import java.util.Map;

public final class CurrencyRepositoryImpl implements CurrencyRepository {

    private final CurrencyDao currencyDao;
    private final CurrencyMapper currencyMapper;
    private final CurrencyValidatorChain currencyValidatorChain;

    public CurrencyRepositoryImpl() {
        currencyDao = DaoFactory.getInstance().getCurrencyDao();
        currencyMapper = MapperFactory.getInstance().getCurrencyMapper();
        currencyValidatorChain = CurrencyValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Currency> getAllWhere(String name, String symbol) {
        var filter = new CurrencyFilterDto(name, symbol);
        List<CurrencyEntity> currencyEntities = currencyDao.findAll(filter);
        return currencyEntities.stream().map(currencyMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Currency get(Integer id) {
        CurrencyEntity currencyEntity = currencyDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти валюту по ідентифікатору"));
        return currencyMapper.toDomain(currencyEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Currency> getAll() {
        List<CurrencyEntity> currencyEntities = currencyDao.findAll();
        return currencyEntities.stream().map(currencyMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param currency persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Currency add(Currency currency) {
        if (!currencyValidatorChain.validate(currency)) {
            throw new DomainAddException("Не вдалось додати валюту із-за не валідних даних");
        }
        currency.setId(null);
        var currencyEntity = currencyMapper.toEntity(currency);
        currencyEntity = currencyDao.save(currencyEntity);
        return currencyMapper.toDomain(currencyEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param currency persistent entity
     */
    @Override
    public void set(Integer id, Currency currency) {
        if (!currencyValidatorChain.validate(currency)) {
            throw new DomainAddException("Не вдалось оновити валюту із-за не валідних даних");
        }
        currency.setId(id);
        get(id);

        var currencyEntity = currencyMapper.toEntity(currency);
        currencyDao.save(currencyEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        get(id);
        currencyDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return currencyValidatorChain.getValidationMessages();
    }
}
