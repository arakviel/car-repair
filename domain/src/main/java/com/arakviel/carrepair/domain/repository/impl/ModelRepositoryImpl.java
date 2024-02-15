package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Brand;
import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.mapper.BrandMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.ModelMapper;
import com.arakviel.carrepair.domain.repository.ModelRepository;
import com.arakviel.carrepair.domain.validator.model.ModelValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.ModelDao;
import com.arakviel.carrepair.persistence.entity.impl.ModelEntity;
import com.arakviel.carrepair.persistence.filter.impl.ModelFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ModelRepositoryImpl implements ModelRepository {

    private final ModelDao modelDao;
    private final ModelMapper modelMapper;
    private final ModelValidatorChain modelValidatorChain;

    public ModelRepositoryImpl() {
        modelDao = DaoFactory.getInstance().getModelDao();
        modelMapper = MapperFactory.getInstance().getModelMapper();
        modelValidatorChain = ModelValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Model> getAllWhere(Brand brand, String name) {
        BrandMapper brandMapper = MapperFactory.getInstance().getBrandMapper();
        var filter = new ModelFilterDto(Objects.nonNull(brand) ? brandMapper.toEntity(brand) : null, name);
        List<ModelEntity> modelEntities = modelDao.findAll(filter);
        return modelEntities.stream().map(modelMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Model get(Integer id) {
        ModelEntity modelEntity = modelDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти модель по ідентифікатору"));
        return modelMapper.toDomain(modelEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Model> getAll() {
        List<ModelEntity> modelEntities = modelDao.findAll();
        return modelEntities.stream().map(modelMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param model persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Model add(Model model) {
        if (!modelValidatorChain.validate(model)) {
            throw new DomainAddException("Не вдалось додати модель із-за не валідних даних");
        }
        model.setId(null);
        var modelEntity = modelMapper.toEntity(model);
        modelEntity = modelDao.save(modelEntity);
        return modelMapper.toDomain(modelEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param model persistent entity
     */
    @Override
    public void set(Integer id, Model model) {
        if (!modelValidatorChain.validate(model)) {
            throw new DomainAddException("Не вдалось оновити модель із-за не валідних даних");
        }
        model.setId(id);
        get(id);

        var modelEntity = modelMapper.toEntity(model);
        modelDao.save(modelEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        get(id);
        modelDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return modelValidatorChain.getValidationMessages();
    }
}
