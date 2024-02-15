package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.RoleEntity;
import com.arakviel.carrepair.persistence.filter.impl.RoleFilterDto;

public interface RoleDao extends GenericDao<Integer, RoleEntity, RoleFilterDto> {}
