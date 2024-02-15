package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.UserEntity;
import com.arakviel.carrepair.persistence.filter.impl.UserFilterDto;
import java.util.UUID;

public interface UserDao extends GenericDao<UUID, UserEntity, UserFilterDto> {}
