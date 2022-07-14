package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class PatientService {
    private final PatientRepository repository;
    private final IcdService icdService;

    @Autowired
    public PatientService(PatientRepository repository,
                          @Lazy IcdService icdService) {
        this.repository = repository;
        this.icdService = icdService;
    }

    private static Calendar localDateToDate(LocalDateTime localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //noinspection MagicConstant
        calendar.set(
                localDate.getYear(),
                localDate.getMonthValue() - 1,
                localDate.getDayOfMonth()
        );

        return calendar;
    }

    @PostConstruct
    private void init() {
        log.info("PatientService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("PatientService is destruction");
    }

    public Set<Patient> findAll() {
        Set<Patient> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all patients from database");
        } else {
            log.info("Patient is not found in database");
        }

        return entities;
    }

    public Set<Patient> findAllByIcd(Integer icdId) {
        Icd icd = icdService.getById(icdId);
        Set<Patient> entities = new TreeSet<>(repository.findAllByIcd(icd));

        if (entities.size() > 0) {
            log.info("Get all patients by icd from database");
        } else {
            log.info("Patient by icd is not found in database");
        }

        return entities;
    }

    public Patient getById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get Patient(id={}) from database", optional.get().getId());
            return optional.get();
        } else {
            log.error("Patient(id={}) is not found in database", id);
            throw new NotFoundException("Patient(id=" + id + ") is not found in database"
            );
        }
    }

    @Transactional
    public Patient save(PatientDTO dto) {
        Optional<Patient> optional = repository.findById(dto.getId());
        Icd icd = icdService.getById(dto.getIcdId());
        Patient entity;

        if(optional.isEmpty()){
            entity = new Patient(dto.getSecondName(),
                    dto.getFirstName(),
                    dto.getPatronymic(),
                    dto.getBirthday(),
                    icd,
                    dto.getComment()
                    );
        } else {
            entity = optional.get();
            entity.setSecondName(dto.getSecondName());
            entity.setFirstName(dto.getFirstName());
            entity.setPatronymic(dto.getPatronymic());
            entity.setBirthday(dto.getBirthday());
            entity.setIcd(icd);
            entity.setComment(dto.getComment());
        }

        entity = repository.save(entity);
        log.info("Patient(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Patient(id={}) is deleted in database", id);
        } else {
            log.error("Patient(id={}) not found in database", id);
            throw new NotFoundException("Patient(id=" + id + ") not found in database");
        }
    }
}
