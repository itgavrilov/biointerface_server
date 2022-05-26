package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.Device;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

}
