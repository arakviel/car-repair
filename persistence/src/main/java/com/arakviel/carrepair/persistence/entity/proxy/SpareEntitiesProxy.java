package com.arakviel.carrepair.persistence.entity.proxy;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SpareEntitiesProxy implements SpareEntities {

    private SpareEntities spareEntities;

    @Override
    public List<SpareEntity> get(UUID orderEntityId) {
        if (Objects.isNull(spareEntities)) {
            spareEntities = oi -> Collections.unmodifiableList(
                    DaoFactory.getInstance().getOrderDao().findAllSpares(oi));
        }
        return spareEntities.get(orderEntityId);
    }
}
