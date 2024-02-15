package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.OrderMapper;
import com.arakviel.carrepair.domain.repository.OrderRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.domain.validator.order.OrderValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.OrderDao;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.OrderFilterDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class OrderRepositoryImpl implements OrderRepository {
    private final OrderDao orderDao;
    private final OrderMapper orderMapper;
    private final OrderValidatorChain orderValidatorChain;

    public OrderRepositoryImpl() {
        orderDao = DaoFactory.getInstance().getOrderDao();
        orderMapper = MapperFactory.getInstance().getOrderMapper();
        orderValidatorChain = OrderValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Order> getAllWhere(
            Client client,
            Car car,
            Discount discount,
            Money price,
            String paymentType,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        var clientMapper = MapperFactory.getInstance().getClientMapper();
        var carMapper = MapperFactory.getInstance().getCarMapper();
        var discountMapper = MapperFactory.getInstance().getDiscountMapper();

        var filter = new OrderFilterDto(
                Objects.nonNull(client) ? clientMapper.toEntity(client) : null,
                Objects.nonNull(car) ? carMapper.toEntity(car) : null,
                Objects.nonNull(discount) ? discountMapper.toEntity(discount) : null,
                Objects.nonNull(price)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                price.wholePart(), price.decimalPart())
                        : null,
                paymentType,
                paymentAt,
                updatedAt,
                createdAt);
        List<OrderEntity> orderEntities = orderDao.findAll(filter);
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Order get(UUID id) {
        OrderEntity orderEntity = orderDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти замовлення по ідентифікатору"));
        return orderMapper.toDomain(orderEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Order> getAll() {
        List<OrderEntity> orderEntities = orderDao.findAll();
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    @Override
    public List<Order> getAllByWorkroom(Workroom curentWorkroom) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(curentWorkroom);
        var orderEntities = orderDao.findAllByWorkroomEntity(workroomEntity);
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    public List<Order> getAllByWorkroom() {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всі замовлення по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroom(curentWorkroom);
    }

    @Override
    public List<Order> getAllByWorkroomWhere(
            Workroom workroom,
            Client client,
            Car car,
            Discount discount,
            Money price,
            String paymentType,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        var clientMapper = MapperFactory.getInstance().getClientMapper();
        var carMapper = MapperFactory.getInstance().getCarMapper();
        var discountMapper = MapperFactory.getInstance().getDiscountMapper();

        var filter = new OrderFilterDto(
                Objects.nonNull(client) ? clientMapper.toEntity(client) : null,
                Objects.nonNull(car) ? carMapper.toEntity(car) : null,
                Objects.nonNull(discount) ? discountMapper.toEntity(discount) : null,
                Objects.nonNull(price)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                price.wholePart(), price.decimalPart())
                        : null,
                paymentType,
                paymentAt,
                updatedAt,
                createdAt);
        List<OrderEntity> orderEntities = orderDao.findAllByWorkroomEntity(filter, workroomEntity);
        return orderEntities.stream().map(orderMapper::toDomain).toList();
    }

    @Override
    public List<Order> getAllByWorkroomWhere(
            Client client,
            Car car,
            Discount discount,
            Money price,
            String paymentType,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всі відфільтровані замовлення по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroomWhere(
                curentWorkroom, client, car, discount, price, paymentType, paymentAt, updatedAt, createdAt);
    }

    /**
     * Save the entity to the database table.
     *
     * @param order persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Order add(Order order) {
        if (!orderValidatorChain.validate(order)) {
            throw new DomainAddException("Не вдалось додати замовлення із-за не валідних даних");
        }
        order.setId(null);
        LocalDateTime now = LocalDateTime.now();
        order.setUpdatedAt(now);
        order.setCreatedAt(now);
        var orderEntity = orderMapper.toEntity(order);
        orderEntity = orderDao.save(orderEntity);
        return orderMapper.toDomain(orderEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param order persistent entity
     */
    @Override
    public void set(UUID id, Order order) {
        if (!orderValidatorChain.validate(order)) {
            throw new DomainAddException("Не вдалось оновити замовлення із-за не валідних даних");
        }
        order.setId(id);
        get(id);

        var orderEntity = orderMapper.toEntity(order);
        orderDao.save(orderEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        orderDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return orderValidatorChain.getValidationMessages();
    }

    @Override
    public List<Employee> getAllStaff(UUID orderId) {
        var employeeEntities = orderDao.findAllStaff(orderId);
        var employeeMapper = MapperFactory.getInstance().getEmployeeMapper();
        return employeeEntities.stream().map(employeeMapper::toDomain).toList();
    }

    @Override
    public void attachToEmployee(UUID orderId, UUID employeeId) {
        orderDao.attachToEmployee(orderId, employeeId);
    }

    @Override
    public void detachFromEmployee(UUID orderId, UUID employeeId) {
        orderDao.detachFromEmployee(orderId, employeeId);
    }

    @Override
    public List<Service> getAllServices(UUID orderId) {
        var serviceEntities = orderDao.findAllServices(orderId);
        var serviceMapper = MapperFactory.getInstance().getServiceMapper();
        return serviceEntities.stream().map(serviceMapper::toDomain).toList();
    }

    @Override
    public String getServiceOrderDescription(UUID orderId, Integer serviceId) {
        return orderDao.findServiceOrderDescription(orderId, serviceId);
    }

    @Override
    public void attachToService(UUID orderId, Integer serviceId, String description) {
        orderDao.attachToService(orderId, serviceId, description);
    }

    @Override
    public void detachFromService(UUID orderId, Integer serviceId) {
        orderDao.detachFromService(orderId, serviceId);
    }

    @Override
    public List<Spare> getAllSpares(UUID orderId) {
        var spareEntities = orderDao.findAllSpares(orderId);
        var spareMapper = MapperFactory.getInstance().getSpareMapper();
        return spareEntities.stream().map(spareMapper::toDomain).toList();
    }

    @Override
    public int getSpareOrderQuantity(UUID orderId, UUID spareId) {
        return orderDao.findSpareOrderQuantity(orderId, spareId);
    }

    @Override
    public void attachToSpare(UUID orderId, UUID spareId, int quantity) {
        orderDao.attachToSpare(orderId, spareId, quantity);
    }

    @Override
    public void detachFromSpare(UUID orderId, UUID spareId) {
        orderDao.detachFromSpare(orderId, spareId);
    }
}
