package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.exception.NotFoundException;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    String MASK_NOT_FOUND = "Device(id=%s) is not found";

    default Device getOrThrow(Integer id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }
}
