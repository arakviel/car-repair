package com.arakviel.carrepair.presentation.mapper.impl;

public final class ModelMapperFactory {

    private AddressModelMapper addressModelMapper;
    private BrandModelMapper brandModelMapper;
    private CarPhotoModelMapper carPhotoModelMapper;
    private CarModelMapper carModelMapper;
    private ClientModelMapper clientModelMapper;
    private CurrencyModelMapper currencyModelMapper;
    private DiscountModelMapper discountModelMapper;
    private ModelModelMapper modelModelMapper;
    private OrderModelMapper orderModelMapper;
    private PayrollModelMapper payrollModelMapper;
    private PhoneModelMapper phoneModelMapper;
    private PositionModelMapper positionModelMapper;
    private RoleModelMapper roleModelMapper;
    private ServiceModelMapper serviceModelMapper;
    private SpareModelMapper spareModelMapper;
    private EmployeeModelMapper employeeModelMapper;
    private UserModelMapper userModelMapper;
    private WorkroomModelMapper workroomModelMapper;

    private ModelMapperFactory() {
        addressModelMapper = new AddressModelMapper();
        brandModelMapper = new BrandModelMapper();
        carPhotoModelMapper = new CarPhotoModelMapper();
        carModelMapper = new CarModelMapper();
        clientModelMapper = new ClientModelMapper();
        currencyModelMapper = new CurrencyModelMapper();
        discountModelMapper = new DiscountModelMapper();
        orderModelMapper = new OrderModelMapper();
        payrollModelMapper = new PayrollModelMapper();
        phoneModelMapper = new PhoneModelMapper();
        roleModelMapper = new RoleModelMapper();
        modelModelMapper = new ModelModelMapper();
        positionModelMapper = new PositionModelMapper();
        serviceModelMapper = new ServiceModelMapper();
        spareModelMapper = new SpareModelMapper();
        employeeModelMapper = new EmployeeModelMapper();
        userModelMapper = new UserModelMapper();
        workroomModelMapper = new WorkroomModelMapper();
    }

    public AddressModelMapper getAddressModelMapper() {
        return addressModelMapper;
    }

    public BrandModelMapper getBrandModelMapper() {
        return brandModelMapper;
    }

    public CarPhotoModelMapper getCarPhotoModelMapper() {
        return carPhotoModelMapper;
    }

    public CarModelMapper getCarModelMapper() {
        return carModelMapper;
    }

    public ClientModelMapper getClientModelMapper() {
        return clientModelMapper;
    }

    public CurrencyModelMapper getCurrencyModelMapper() {
        return currencyModelMapper;
    }

    public DiscountModelMapper getDiscountModelMapper() {
        return discountModelMapper;
    }

    public ModelModelMapper getModelModelMapper() {
        return modelModelMapper;
    }

    public OrderModelMapper getOrderModelMapper() {
        return orderModelMapper;
    }

    public PayrollModelMapper getPayrollModelMapper() {
        return payrollModelMapper;
    }

    public PhoneModelMapper getPhoneModelMapper() {
        return phoneModelMapper;
    }

    public PositionModelMapper getPositionModelMapper() {
        return positionModelMapper;
    }

    public RoleModelMapper getRoleModelMapper() {
        return roleModelMapper;
    }

    public ServiceModelMapper getServiceModelMapper() {
        return serviceModelMapper;
    }

    public SpareModelMapper getSpareModelMapper() {
        return spareModelMapper;
    }

    public EmployeeModelMapper getEmployeeModelMapper() {
        return employeeModelMapper;
    }

    public UserModelMapper getUserModelMapper() {
        return userModelMapper;
    }

    public WorkroomModelMapper getWorkroomModelMapper() {
        return workroomModelMapper;
    }

    private static class SingletonHolder {
        public static final ModelMapperFactory INSTANCE = new ModelMapperFactory();
    }

    public static ModelMapperFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
