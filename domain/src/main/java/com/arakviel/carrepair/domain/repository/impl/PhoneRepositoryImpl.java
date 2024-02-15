package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.mapper.EmployeeMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.PhoneMapper;
import com.arakviel.carrepair.domain.repository.PhoneRepository;
import com.arakviel.carrepair.domain.validator.phone.PhoneValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.PhoneDao;
import com.arakviel.carrepair.persistence.entity.impl.PhoneEntity;
import com.arakviel.carrepair.persistence.filter.impl.PhoneFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PhoneRepositoryImpl implements PhoneRepository {

    private final PhoneDao phoneDao;
    private final PhoneMapper phoneMapper;
    private final PhoneValidatorChain phoneValidatorChain;

    public PhoneRepositoryImpl() {
        phoneDao = DaoFactory.getInstance().getPhoneDao();
        phoneMapper = MapperFactory.getInstance().getPhoneMapper();
        phoneValidatorChain = PhoneValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Phone> getAllWhere(Employee employee, String type, String value) {
        EmployeeMapper employeeMapper = MapperFactory.getInstance().getEmployeeMapper();
        var filter =
                new PhoneFilterDto(Objects.nonNull(employee) ? employeeMapper.toEntity(employee) : null, type, value);
        List<PhoneEntity> phoneEntities = phoneDao.findAll(filter);
        return phoneEntities.stream().map(phoneMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Phone get(UUID id) {
        PhoneEntity phoneEntity = phoneDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти телефон по ідентифікатору"));
        return phoneMapper.toDomain(phoneEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Phone> getAll() {
        List<PhoneEntity> phoneEntities = phoneDao.findAll();
        return phoneEntities.stream().map(phoneMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param phone persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Phone add(Phone phone) {
        if (!phoneValidatorChain.validate(phone)) {
            throw new DomainAddException("Не вдалось додати телефон із-за не валідних даних");
        }
        phone.setId(null);
        var phoneEntity = phoneMapper.toEntity(phone);
        phoneEntity = phoneDao.save(phoneEntity);
        return phoneMapper.toDomain(phoneEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param phone persistent entity
     */
    @Override
    public void set(UUID id, Phone phone) {
        if (!phoneValidatorChain.validate(phone)) {
            throw new DomainAddException("Не вдалось оновити телефон із-за не валідних даних");
        }
        phone.setId(id);
        get(id);

        var phoneEntity = phoneMapper.toEntity(phone);
        phoneDao.save(phoneEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        phoneDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return phoneValidatorChain.getValidationMessages();
    }
}
