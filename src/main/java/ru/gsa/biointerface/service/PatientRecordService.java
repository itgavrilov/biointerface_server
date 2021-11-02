package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Service
public class PatientRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecordService.class);
    private final PatientRecordRepository repository;

    @Autowired
    public PatientRecordService(PatientRecordRepository repository) {
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
        LOGGER.info("PatientRecordService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("PatientRecordService is destruction");
    }

    public List<PatientRecord> getAll() throws Exception {
        List<PatientRecord> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all patientRecords from database");
        } else {
            LOGGER.info("PatientRecords is not found in database");
        }

        return entities;
    }

    public PatientRecord findById(Long id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<PatientRecord> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get patientRecord(id={}) from database", optional.get().getId());
            return optional.get();
        } else {
            LOGGER.error("PatientRecord(id={}) is not found in database", id);
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + id + ") is not found in database"
            );
        }
    }

    @Transactional
    public void save(PatientRecord entity) throws Exception {
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
        LOGGER.info("PatientRecord(id={}) is recorded in database", entity.getId());
    }

    @Transactional
    public void delete(PatientRecord entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<PatientRecord> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("PatientRecord(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + entity.getId() + ") not found in database"
            );
        }
    }
}
