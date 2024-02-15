package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClientRepository extends GenericRepository<UUID, Client> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Client> getAllWhere(
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);

    List<Client> getAllByWorkroom(Workroom workroom);

    List<Client> getAllByWorkroom();

    List<Client> getAllByWorkroomWhere(
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);

    List<Client> getAllByWorkroomWhere(
            Workroom workroom,
            String phone,
            String email,
            String firstName,
            String lastName,
            String middleName,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);
}
