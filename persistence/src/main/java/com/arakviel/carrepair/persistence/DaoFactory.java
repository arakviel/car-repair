package com.arakviel.carrepair.persistence;

import com.arakviel.carrepair.persistence.dao.AddressDao;
import com.arakviel.carrepair.persistence.dao.BrandDao;
import com.arakviel.carrepair.persistence.dao.CarDao;
import com.arakviel.carrepair.persistence.dao.CarPhotoDao;
import com.arakviel.carrepair.persistence.dao.ClientDao;
import com.arakviel.carrepair.persistence.dao.CurrencyDao;
import com.arakviel.carrepair.persistence.dao.DiscountDao;
import com.arakviel.carrepair.persistence.dao.EmployeeDao;
import com.arakviel.carrepair.persistence.dao.ModelDao;
import com.arakviel.carrepair.persistence.dao.OrderDao;
import com.arakviel.carrepair.persistence.dao.PayrollDao;
import com.arakviel.carrepair.persistence.dao.PhoneDao;
import com.arakviel.carrepair.persistence.dao.PositionDao;
import com.arakviel.carrepair.persistence.dao.RoleDao;
import com.arakviel.carrepair.persistence.dao.ServiceDao;
import com.arakviel.carrepair.persistence.dao.SpareDao;
import com.arakviel.carrepair.persistence.dao.UserDao;
import com.arakviel.carrepair.persistence.dao.WorkroomDao;
import com.arakviel.carrepair.persistence.impl.AddressDaoImpl;
import com.arakviel.carrepair.persistence.impl.BrandDaoImpl;
import com.arakviel.carrepair.persistence.impl.CarDaoImpl;
import com.arakviel.carrepair.persistence.impl.CarPhotoDaoImpl;
import com.arakviel.carrepair.persistence.impl.ClientDaoImpl;
import com.arakviel.carrepair.persistence.impl.CurrencyDaoImpl;
import com.arakviel.carrepair.persistence.impl.DiscountDaoImpl;
import com.arakviel.carrepair.persistence.impl.EmployeeDaoImpl;
import com.arakviel.carrepair.persistence.impl.ModelDaoImpl;
import com.arakviel.carrepair.persistence.impl.OrderDaoImpl;
import com.arakviel.carrepair.persistence.impl.PayrollDaoImpl;
import com.arakviel.carrepair.persistence.impl.PhoneDaoImpl;
import com.arakviel.carrepair.persistence.impl.PositionDaoImpl;
import com.arakviel.carrepair.persistence.impl.RoleDaoImpl;
import com.arakviel.carrepair.persistence.impl.ServiceDaoImpl;
import com.arakviel.carrepair.persistence.impl.SpareDaoImpl;
import com.arakviel.carrepair.persistence.impl.UserDaoImpl;
import com.arakviel.carrepair.persistence.impl.WorkroomDaoImpl;
import com.arakviel.carrepair.persistence.util.ConnectionManager;

public class DaoFactory {

    public AddressDao getAddressDao() {
        return AddressDaoImpl.getInstance();
    }

    public BrandDao getBrandDao() {
        return BrandDaoImpl.getInstance();
    }

    public CarDao getCarDao() {
        return CarDaoImpl.getInstance();
    }

    public CarPhotoDao getCarPhotoDao() {
        return CarPhotoDaoImpl.getInstance();
    }

    public ClientDao getClientDao() {
        return ClientDaoImpl.getInstance();
    }

    public CurrencyDao getCurrencyDao() {
        return CurrencyDaoImpl.getInstance();
    }

    public DiscountDao getDiscountDao() {
        return DiscountDaoImpl.getInstance();
    }

    public ModelDao getModelDao() {
        return ModelDaoImpl.getInstance();
    }

    public OrderDao getOrderDao() {
        return OrderDaoImpl.getInstance();
    }

    public PayrollDao getPayrollDao() {
        return PayrollDaoImpl.getInstance();
    }

    public PhoneDao getPhoneDao() {
        return PhoneDaoImpl.getInstance();
    }

    public PositionDao getPositionDao() {
        return PositionDaoImpl.getInstance();
    }

    public RoleDao getRoleDao() {
        return RoleDaoImpl.getInstance();
    }

    public ServiceDao getServiceDao() {
        return ServiceDaoImpl.getInstance();
    }

    public SpareDao getSpareDao() {
        return SpareDaoImpl.getInstance();
    }

    public EmployeeDao getEmployeeDao() {
        return EmployeeDaoImpl.getInstance();
    }

    public UserDao getUserDao() {
        return UserDaoImpl.getInstance();
    }

    public WorkroomDao getWorkroomDao() {
        return WorkroomDaoImpl.getInstance();
    }

    public void closeAllDaoConnections() {
        ConnectionManager.closePool();
    }

    // ...

    private DaoFactory() {}

    private static class SingletonHolder {
        public static final DaoFactory INSTANCE = new DaoFactory();
    }

    public static DaoFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
