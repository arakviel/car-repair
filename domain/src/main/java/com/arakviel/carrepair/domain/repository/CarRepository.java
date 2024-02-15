package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CarRepository extends GenericRepository<UUID, Car> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Car> getAllWhere(
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);

    List<Car> getAllByWorkroom(Workroom workroom);

    List<Car> getAllByWorkroom();

    public List<Car> getAllByWorkroomWhere(
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);

    List<Car> getAllByWorkroomWhere(
            Workroom workroom,
            Model model,
            String number,
            Short year,
            String engineType,
            Integer mileage,
            Color color,
            LocalDateTime updatedAt,
            LocalDateTime createdAt);
}
