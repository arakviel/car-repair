package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.impl.Workroom;
import java.util.List;
import java.util.UUID;

public interface CarPhotoRepository extends GenericRepository<UUID, CarPhoto> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<CarPhoto> getAllWhere(Car car);

    List<CarPhoto> getAllByWorkroom(Workroom workroom);

    List<CarPhoto> getAllByWorkroom();

    List<CarPhoto> getAllByWorkroomWhere(Workroom workroom, Car car);

    List<CarPhoto> getAllByWorkroomWhere(Car car);
}
