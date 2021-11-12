package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.PatientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class PatientService {
    private final PatientRepository repository;

    @Autowired
    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    private static Calendar localDateToDate(LocalDate localDate) {
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

    public List<Patient> findAll() throws Exception {
        List<Patient> entities = repository.findAll();

        if (entities.size() > 0) {
            log.info("Get all patients from database");
        } else {
            log.info("Patient is not found in database");
        }

        return entities;
    }

    public Patient findById(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get Patient(id={}) from database", optional.get().getId());
            return optional.get();
        } else {
            log.error("Patient(id={}) is not found in database", id);
            throw new EntityNotFoundException(
                    "Patient(id=" + id + ") is not found in database"
            );
        }
    }

    @Transactional
    public void save(Patient entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if (entity.getSecondName().isBlank())
            throw new IllegalArgumentException("SecondName is blank");
        if (entity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if (entity.getFirstName().isBlank())
            throw new IllegalArgumentException("FirstName is blank");
        if (entity.getPatronymic() == null)
            throw new NullPointerException("MiddleName is null");
        if (entity.getPatronymic().isBlank())
            throw new IllegalArgumentException("MiddleName is blank");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        repository.save(entity);
        log.info("Patient(id={}) is recorded in database", entity.getId());
    }

    @Transactional
    public void delete(Patient entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Patient(id={}) is deleted in database", optional.get().getId());
        } else {
            log.error("Patient(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "Patient(id=" + entity.getId() + ") not found in database"
            );
        }
    }
}
