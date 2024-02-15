package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.UserMapper;
import com.arakviel.carrepair.persistence.entity.impl.UserEntity;
import java.util.Objects;

public class UserMapperImpl implements UserMapper {

    @Override
    public User toDomain(UserEntity entity) {
        User user = User.builder()
                .id(Objects.nonNull(entity.getId()) ? entity.getId() : null)
                .email(entity.getEmail())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .build();
        if (Objects.nonNull(entity.getEmployeeEntity())) {
            user.setEmployee(MapperFactory.getInstance().getEmployeeMapper().toDomain(entity.getEmployeeEntity()));
        }
        return user;
    }

    @Override
    public UserEntity toEntity(User domain) {
        UserEntity userEntity = UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .login(domain.getLogin())
                .password(domain.getPassword())
                .build();
        if (Objects.nonNull(domain.getEmployee())) {
            userEntity.setEmployeeEntity(
                    MapperFactory.getInstance().getEmployeeMapper().toEntity(domain.getEmployee()));
        }
        return userEntity;
    }

    private UserMapperImpl() {}

    private static class SingletonHolder {
        public static final UserMapperImpl INSTANCE = new UserMapperImpl();
    }

    public static UserMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
