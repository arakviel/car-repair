package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Role;
import java.util.List;

public interface RoleRepository extends GenericRepository<Integer, Role> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Role> getAllWhere(
            Boolean canEditUsers,
            Boolean canEditSpares,
            Boolean canEditClients,
            Boolean canEditServices,
            Boolean canEditOrders,
            Boolean canEditPayrolls);
}
