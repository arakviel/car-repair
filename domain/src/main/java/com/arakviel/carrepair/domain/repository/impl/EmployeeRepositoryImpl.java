package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.EmployeeMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.repository.EmployeeRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.domain.validator.employee.EmployeeValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.EmployeeDao;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.filter.impl.EmployeeFilterDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class EmployeeRepositoryImpl implements EmployeeRepository {
    private final EmployeeDao employeeDao;
    private final EmployeeMapper employeeMapper;
    private final EmployeeValidatorChain employeeValidatorChain;

    public EmployeeRepositoryImpl() {
        employeeDao = DaoFactory.getInstance().getEmployeeDao();
        employeeMapper = MapperFactory.getInstance().getEmployeeMapper();
        employeeValidatorChain = EmployeeValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Employee> getAllWhere(
            Address address,
            Workroom workroom,
            Position position,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        var addressMapper = MapperFactory.getInstance().getAddressMapper();
        var workroomMapper = MapperFactory.getInstance().getWorkroomMapper();
        var positionMapper = MapperFactory.getInstance().getPositionMapper();
        var filter = new EmployeeFilterDto(
                Objects.nonNull(address) ? addressMapper.toEntity(address) : null,
                Objects.nonNull(workroom) ? workroomMapper.toEntity(workroom) : null,
                Objects.nonNull(position) ? positionMapper.toEntity(position) : null,
                firstName,
                lastName,
                middleName,
                updatedAt,
                createdAt);
        List<EmployeeEntity> employeeEntities = employeeDao.findAll(filter);
        return employeeEntities.stream().map(employeeMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Employee get(UUID id) {
        EmployeeEntity employeeEntity = employeeDao
                .findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти працівника по ідентифікатору"));
        return employeeMapper.toDomain(employeeEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Employee> getAll() {
        var employeeEntities = employeeDao.findAll();
        return employeeEntities.stream().map(employeeMapper::toDomain).toList();
    }

    @Override
    public List<Employee> getAllByWorkroom(Workroom workroom) {
        return getAllWhere(null, workroom, null, null, null, null, null, null);
    }

    @Override
    public List<Employee> getAllByWorkroom() {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всіх співробітників по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroom(curentWorkroom);
    }

    /**
     * Save the entity to the database table.
     *
     * @param employee persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Employee add(Employee employee) {
        if (!employeeValidatorChain.validate(employee)) {
            throw new DomainAddException("Не вдалось додати працівника із-за не валідних даних");
        }
        employee.setId(null);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setCreatedAt(now);
        var employeeEntity = employeeMapper.toEntity(employee);
        employeeEntity = employeeDao.save(employeeEntity);
        return employeeMapper.toDomain(employeeEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param employee persistent entity
     */
    @Override
    public void set(UUID id, Employee employee) {
        if (!employeeValidatorChain.validate(employee)) {
            throw new DomainAddException("Не вдалось оновити працівника із-за не валідних даних");
        }
        employee.setId(id);
        get(id);

        var employeeEntity = employeeMapper.toEntity(employee);
        employeeDao.save(employeeEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        employeeDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return employeeValidatorChain.getValidationMessages();
    }

    @Override
    public List<Order> getAllOrders(UUID employeeId) {
        var orderEntities = employeeDao.findAllOrders(employeeId);
        var orderMapper = MapperFactory.getInstance().getOrderMapper();
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    @Override
    public void attachToOrder(UUID employeeId, UUID orderId) {
        employeeDao.attachToOrder(employeeId, orderId);
    }

    @Override
    public void detachFromOrder(UUID employeeId, UUID orderId) {
        employeeDao.detachFromOrder(employeeId, orderId);
    }
}
