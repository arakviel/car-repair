package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Role;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.RoleMapper;
import com.arakviel.carrepair.domain.repository.RoleRepository;
import com.arakviel.carrepair.domain.validator.role.RoleValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.RoleDao;
import com.arakviel.carrepair.persistence.entity.impl.RoleEntity;
import com.arakviel.carrepair.persistence.filter.impl.RoleFilterDto;
import java.util.List;
import java.util.Map;

public final class RoleRepositoryImpl implements RoleRepository {

    private final RoleDao roleDao;
    private final RoleMapper roleMapper;
    private final RoleValidatorChain roleValidatorChain;

    public RoleRepositoryImpl() {
        roleDao = DaoFactory.getInstance().getRoleDao();
        roleMapper = MapperFactory.getInstance().getRoleMapper();
        roleValidatorChain = RoleValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Role> getAllWhere(
            Boolean canEditUsers,
            Boolean canEditSpares,
            Boolean canEditClients,
            Boolean canEditServices,
            Boolean canEditOrders,
            Boolean canEditPayrolls) {
        var filter = new RoleFilterDto(
                canEditUsers, canEditSpares, canEditClients, canEditServices, canEditOrders, canEditPayrolls);
        List<RoleEntity> roleEntities = roleDao.findAll(filter);
        return roleEntities.stream().map(roleMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Role get(Integer id) {
        RoleEntity roleEntity = roleDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти професію по ідентифікатору"));
        return roleMapper.toDomain(roleEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Role> getAll() {
        List<RoleEntity> roleEntities = roleDao.findAll();
        return roleEntities.stream().map(roleMapper::toDomain).toList();
    }

    /**
     * Save the entity to the database table.
     *
     * @param role persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Role add(Role role) {
        if (!roleValidatorChain.validate(role)) {
            throw new DomainAddException("Не вдалось додати професію із-за не валідних даних");
        }

        var roleEntity = roleMapper.toEntity(role);
        roleEntity = roleDao.save(roleEntity);
        return roleMapper.toDomain(roleEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param role persistent entity
     */
    @Override
    public void set(Integer id, Role role) {
        if (!roleValidatorChain.validate(role)) {
            throw new DomainAddException("Не вдалось оновити професію із-за не валідних даних");
        }
        role.setId(id);
        get(id);

        var roleEntity = roleMapper.toEntity(role);
        roleDao.save(roleEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        get(id);
        roleDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return roleValidatorChain.getValidationMessages();
    }
}
