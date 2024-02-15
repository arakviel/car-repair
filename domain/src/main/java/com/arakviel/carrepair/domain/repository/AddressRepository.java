package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.util.List;
import java.util.UUID;

public interface AddressRepository extends GenericRepository<UUID, Address> {
    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Address> getAllWhere(String country, String region, String city, String street, String home);

    List<Address> getAllByWorkroom(Workroom workroom);

    List<Address> getAllByWorkroom();

    List<Address> getAllByWorkroomWhere(String country, String region, String city, String street, String home);

    List<Address> getAllByWorkroomWhere(
            Workroom workroom, String country, String region, String city, String street, String home);
}
