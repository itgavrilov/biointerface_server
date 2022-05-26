package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.domain.Patient;

import java.util.List;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findAllByIcd(Icd icd);
}
