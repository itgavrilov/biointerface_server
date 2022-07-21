package ru.gsa.biointerface.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.exception.NotFoundException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Integer> {

    String MASK_NOT_FOUND = "Examination(id=%s) is not found";

    default Examination getOrThrow(Integer id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }

    @Query(nativeQuery = true,
            value = "select * from examination as e " +
                    "where (:patientId is null or e.patient_id = :patientId) " +
                    "and (:deviceId is null or e.device_id = :deviceId) ")
    List<Examination> findAllByPatientIdAndDeviceId(@Param("patientId") Integer patientId,
                                                    @Param("deviceId") Integer deviceId);

    @Query(nativeQuery = true,
            value = "select * from examination as e " +
                    "where (:patientId is null or e.patient_id = :patientId) " +
                    "and (:deviceId is null or e.device_id = :deviceId) ")
    Page<Examination> findAllByPatientIdAndDeviceId(@Param("patientId") Integer patientId,
                                                    @Param("deviceId") Integer deviceId,
                                                    Pageable pageable);
}
