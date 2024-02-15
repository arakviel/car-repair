package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.CarMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.ModelMapper;
import com.arakviel.carrepair.domain.repository.CarRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.domain.validator.car.CarValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.CarDao;
import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.CarFilterDto;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CarRepositoryImpl implements CarRepository {

    private final CarDao carDao;
    private final CarMapper carMapper;
    private final CarValidatorChain carValidatorChain;

    public CarRepositoryImpl() {
        carDao = DaoFactory.getInstance().getCarDao();
        carMapper = MapperFactory.getInstance().getCarMapper();
        carValidatorChain = CarValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Car> getAllWhere(
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        ModelMapper modelMapper = MapperFactory.getInstance().getModelMapper();
        var filter = new CarFilterDto(
                Objects.nonNull(model) ? modelMapper.toEntity(model) : null,
                number,
                year,
                engineType,
                mileage,
                color,
                updatedAt,
                createdAt);
        List<CarEntity> carEntities = carDao.findAll(filter);
        return carEntities.stream().map(carMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Car get(UUID id) {
        CarEntity carEntity = carDao.findOneById(id)
                .orElseThrow(() -> new DomainNotFoundException("Не вдалось знайти машину по ідентифікатору"));
        return carMapper.toDomain(carEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Car> getAll() {
        List<CarEntity> carEntities = carDao.findAll();
        return carEntities.stream().map(carMapper::toDomain).toList();
    }

    @Override
    public List<Car> getAllByWorkroom(Workroom workroom) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        List<CarEntity> carEntities = carDao.findAllByWorkroomEntity(workroomEntity);
        return carEntities.stream().map(carMapper::toDomain).toList();
    }

    @Override
    public List<Car> getAllByWorkroom() {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всі автомобілі по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroom(curentWorkroom);
    }

    @Override
    public List<Car> getAllByWorkroomWhere(
            Workroom workroom,
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        var workroomMapper = MapperFactory.getInstance().getWorkroomMapper();
        ModelMapper modelMapper = MapperFactory.getInstance().getModelMapper();
        var filter = new CarFilterDto(
                Objects.nonNull(model) ? modelMapper.toEntity(model) : null,
                number,
                year,
                engineType,
                mileage,
                color,
                updatedAt,
                createdAt);
        List<CarEntity> carEntities = carDao.findAllByWorkroomEntity(
                filter, Objects.nonNull(workroom) ? workroomMapper.toEntity(workroom) : null);
        return carEntities.stream().map(carMapper::toDomain).toList();
    }

    @Override
    public List<Car> getAllByWorkroomWhere(
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всі автомобілі по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroomWhere(
                curentWorkroom, model, number, year, engineType, mileage, color, updatedAt, createdAt);
    }

    /**
     * Save the entity to the database table.
     *
     * @param car persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Car add(Car car) {
        if (!carValidatorChain.validate(car)) {
            throw new DomainAddException("Не вдалось додати машину із-за не валідних даних");
        }
        car.setId(null);
        LocalDateTime now = LocalDateTime.now();
        car.setUpdatedAt(now);
        car.setCreatedAt(now);
        var carEntity = carMapper.toEntity(car);
        carEntity = carDao.save(carEntity);
        return carMapper.toDomain(carEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param car persistent entity
     */
    @Override
    public void set(UUID id, Car car) {
        if (!carValidatorChain.validate(car)) {
            throw new DomainAddException("Не вдалось оновити машину із-за не валідних даних");
        }
        car.setId(id);
        get(id);

        var carEntity = carMapper.toEntity(car);
        carDao.save(carEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        carDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return carValidatorChain.getValidationMessages();
    }
}
