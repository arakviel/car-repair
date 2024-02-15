package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.util.List;
import java.util.UUID;

public interface WorkroomRepository extends GenericRepository<UUID, Workroom> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Workroom> getAllWhere(Address address, String name);
}
