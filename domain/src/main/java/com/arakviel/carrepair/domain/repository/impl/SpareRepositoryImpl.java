package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.SpareMapper;
import com.arakviel.carrepair.domain.repository.SpareRepository;
import com.arakviel.carrepair.domain.validator.spare.SpareValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.SpareDao;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.filter.impl.SpareFilterDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SpareRepositoryImpl implements SpareRepository {
    private final SpareDao spareDao;
    private final SpareMapper spareMapper;
    private final SpareValidatorChain spareValidatorChain;

    public SpareRepositoryImpl() {
        spareDao = DaoFactory.getInstance().getSpareDao();
        spareMapper = MapperFactory.getInstance().getSpareMapper();
        spareValidatorChain = SpareValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Spare> getAllWhere(Workroom workroom, String name, Money price, Integer quantityInStock) {
        var workroomMapper = MapperFactory.getInstance().getWorkroomMapper();
        var filter = new SpareFilterDto(
                Objects.nonNull(workroom) ? workroomMapper.toEntity(workroom) : null,
                name,
                Objects.nonNull(price)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                price.wholePart(), price.decimalPart())
                        : null,
                Objects.nonNull(quantityInStock) ? quantityInStock : null);
        List<SpareEntity> spareEntities = spareDao.findAll(filter);
        return spareEntities.stream().map(spareMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Spare get(UUID id) {
        SpareEntity spareEntity = spareDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти запчастину по ідентифікатору"));
        return spareMapper.toDomain(spareEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Spare> getAll() {
        List<SpareEntity> spareEntities = spareDao.findAll();
        return spareEntities.stream().map(spareMapper::toDomain).toList();
    }

    @Override
    public List<Spare> getAllByWorkroom(Workroom workroom) {
        return getAllWhere(workroom, null, null, null);
    }

    /**
     * Save the entity to the database table.
     *
     * @param spare persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Spare add(Spare spare) {
        if (!spareValidatorChain.validate(spare)) {
            throw new DomainAddException("Не вдалось додати запчастину із-за не валідних даних");
        }
        spare.setId(null);
        var spareEntity = spareMapper.toEntity(spare);
        spareEntity = spareDao.save(spareEntity);
        return spareMapper.toDomain(spareEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param spare persistent entity
     */
    @Override
    public void set(UUID id, Spare spare) {
        if (!spareValidatorChain.validate(spare)) {
            throw new DomainAddException("Не вдалось оновити запчастину із-за не валідних даних");
        }
        spare.setId(id);
        get(id);

        var spareEntity = spareMapper.toEntity(spare);
        spareDao.save(spareEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        spareDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return spareValidatorChain.getValidationMessages();
    }

    @Override
    public List<Order> findAllOrders(UUID spareId) {
        var orderEntities = spareDao.findAllOrders(spareId);
        var orderMapper = MapperFactory.getInstance().getOrderMapper();
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    @Override
    public void attachToOrder(UUID spareId, UUID orderId, int quantity) {
        spareDao.attachToOrder(spareId, orderId, quantity);
    }

    @Override
    public void detachFromOrder(UUID spareId, UUID orderId) {
        spareDao.detachFromOrder(spareId, orderId);
    }
}
