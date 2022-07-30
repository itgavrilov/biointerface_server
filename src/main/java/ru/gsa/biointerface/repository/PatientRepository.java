package ru.gsa.biointerface.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;

import java.util.List;
import java.util.UUID;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    String MASK_NOT_FOUND = "Patient(id=%s) is not found";

    default Patient getOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }

    @Query(nativeQuery = true,
            value = "select * from patient as p " +
                    "where (:icdId is null or p.icd_id = :icdId) ")
    List<Patient> findAllByIcd(@Param("icdId") UUID icdId);

    @Query(nativeQuery = true,
            value = "select * from patient as p " +
                    "where (:icdId is null or p.icd_id = :icdId) ")
    Page<Patient> findAllByIcd(@Param("icdId") UUID icdId,
                               Pageable pageable);
}
