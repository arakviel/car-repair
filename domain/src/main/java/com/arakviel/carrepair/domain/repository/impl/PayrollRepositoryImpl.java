package com.arakviel.carrepair.domain.repository.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainAddException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.EmployeeMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.PayrollMapper;
import com.arakviel.carrepair.domain.repository.PayrollRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.domain.validator.payroll.PayrollValidatorChain;
import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.PayrollDao;
import com.arakviel.carrepair.persistence.entity.impl.PayrollEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.PayrollFilterDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PayrollRepositoryImpl implements PayrollRepository {

    private final PayrollDao payrollDao;
    private final PayrollMapper payrollMapper;
    private final PayrollValidatorChain payrollValidatorChain;

    public PayrollRepositoryImpl() {
        payrollDao = DaoFactory.getInstance().getPayrollDao();
        payrollMapper = MapperFactory.getInstance().getPayrollMapper();
        payrollValidatorChain = PayrollValidatorChain.getInstance();
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    @Override
    public List<Payroll> getAllWhere(
            Employee employee,
            String periodType,
            Integer hourCount,
            Money salary,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        EmployeeMapper employeeMapper = MapperFactory.getInstance().getEmployeeMapper();
        var filter = new PayrollFilterDto(
                Objects.nonNull(employee) ? employeeMapper.toEntity(employee) : null,
                periodType,
                hourCount,
                Objects.nonNull(salary)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                salary.wholePart(), salary.decimalPart())
                        : null,
                paymentAt,
                updatedAt,
                createdAt);
        List<PayrollEntity> payrollEntities = payrollDao.findAll(filter);
        return payrollEntities.stream().map(payrollMapper::toDomain).toList();
    }

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    @Override
    public Payroll get(UUID id) {
        PayrollEntity payrollEntity = payrollDao
                .findOneById(id)
                .orElseThrow(
                        () -> new DomainNotFoundException("Не вдалось знайти заробітню плату по" + " ідентифікатору"));
        return payrollMapper.toDomain(payrollEntity);
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<Payroll> getAll() {
        List<PayrollEntity> payrollEntities = payrollDao.findAll();
        return payrollEntities.stream().map(payrollMapper::toDomain).toList();
    }

    @Override
    public List<Payroll> getAllByWorkroom(Workroom workroom) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        List<PayrollEntity> payrollEntities = payrollDao.findAllByWorkroomEntity(workroomEntity);
        return payrollEntities.stream().map(payrollMapper::toDomain).toList();
    }

    @Override
    public List<Payroll> getAllByWorkroom() {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException("Щоб отримати всі заробітні плати по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroom(curentWorkroom);
    }

    @Override
    public List<Payroll> getAllByWorkroomWhere(
            Workroom workroom,
            Employee employee,
            String periodType,
            Integer hourCount,
            Money salary,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        WorkroomEntity workroomEntity =
                MapperFactory.getInstance().getWorkroomMapper().toEntity(workroom);
        EmployeeMapper employeeMapper = MapperFactory.getInstance().getEmployeeMapper();
        var filter = new PayrollFilterDto(
                Objects.nonNull(employee) ? employeeMapper.toEntity(employee) : null,
                periodType,
                hourCount,
                Objects.nonNull(salary)
                        ? new com.arakviel.carrepair.persistence.entity.impl.Money(
                                salary.wholePart(), salary.decimalPart())
                        : null,
                paymentAt,
                updatedAt,
                createdAt);
        List<PayrollEntity> payrollEntities = payrollDao.findAllByWorkroomEntity(filter, workroomEntity);
        return payrollEntities.stream().map(payrollMapper::toDomain).toList();
    }

    @Override
    public List<Payroll> getAllByWorkroomWhere(
            Employee employee,
            String periodType,
            Integer hourCount,
            Money salary,
            LocalDateTime paymentAt,
            LocalDateTime updatedAt,
            LocalDateTime createdAt) {
        AuthService authService = AuthServiceImpl.getInstance();
        Workroom curentWorkroom = authService.getCurentWorkroom();
        if (Objects.isNull(curentWorkroom)) {
            throw new AuthException(
                    "Щоб отримати всі відфільтровані заробітні плати по відділу, спочатку аутентифікуйтесь");
        }
        return getAllByWorkroomWhere(
                curentWorkroom, employee, periodType, hourCount, salary, paymentAt, updatedAt, createdAt);
    }

    /**
     * Save the entity to the database table.
     *
     * @param payroll persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    @Override
    public Payroll add(Payroll payroll) {
        if (!payrollValidatorChain.validate(payroll)) {
            throw new DomainAddException("Не вдалось додати заробітню плату із-за не валідних даних");
        }
        payroll.setId(null);
        LocalDateTime now = LocalDateTime.now();
        payroll.setCreatedAt(now);
        payroll.setUpdatedAt(now);
        var payrollEntity = payrollMapper.toEntity(payroll);
        payrollEntity = payrollDao.save(payrollEntity);
        return payrollMapper.toDomain(payrollEntity);
    }

    /**
     * Update the domain to the database table.
     *
     * @param id ідентифікатор
     * @param payroll persistent entity
     */
    @Override
    public void set(UUID id, Payroll payroll) {
        if (!payrollValidatorChain.validate(payroll)) {
            throw new DomainAddException("Не вдалось оновити заробітню плату із-за не валідних даних");
        }
        payroll.setId(id);
        get(id);

        var payrollEntity = payrollMapper.toEntity(payroll);
        payrollDao.save(payrollEntity);
    }

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        get(id);
        payrollDao.remove(id);
    }

    @Override
    public Map<String, List<String>> getValidationMessages() {
        return payrollValidatorChain.getValidationMessages();
    }
}
