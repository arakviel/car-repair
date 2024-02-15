package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.PhoneEntity;
import com.arakviel.carrepair.persistence.filter.impl.PhoneFilterDto;
import java.util.UUID;

public interface PhoneDao extends GenericDao<UUID, PhoneEntity, PhoneFilterDto> {}
