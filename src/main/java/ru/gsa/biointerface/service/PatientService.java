package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public Patient save(Patient entity) {
        if (entity == null)
            throw new BadRequestException("Entity is null");
        if (entity.getId() <= 0)
            throw new BadRequestException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new BadRequestException("SecondName is null");
        if (entity.getSecondName().isBlank())
            throw new BadRequestException("SecondName is blank");
        if (entity.getFirstName() == null)
            throw new BadRequestException("FirstName is null");
        if (entity.getFirstName().isBlank())
            throw new BadRequestException("FirstName is blank");
        if (entity.getPatronymic() == null)
            throw new BadRequestException("MiddleName is null");
        if (entity.getPatronymic().isBlank())
            throw new BadRequestException("MiddleName is blank");
        if (entity.getBirthday() == null)
            throw new BadRequestException("Birthday is null");
        if (entity.getExaminations() == null)
            throw new BadRequestException("Examinations is null");

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

    public PatientDTO convertEntityToDto(Patient entity) {
        int icd_id = 0;

        if (entity.getIcd() != null) {
            icd_id = entity.getIcd().getId();
        }

        return PatientDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .patronymic(entity.getPatronymic())
                .birthday(LocalDateTime.ofInstant(
                        entity.getBirthday().toInstant(),
                        ZoneId.systemDefault()))
                .icdId(icd_id)
                .comment(entity.getComment())
                .build();
    }

    public Patient convertDtoToEntity(PatientDTO dto) {
        Icd icd = null;

        if (dto.getIcdId() != 0) {
            icd = icdService.getById(dto.getIcdId());
        }

        Patient patient = Patient.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .secondName(dto.getSecondName())
                .patronymic(dto.getPatronymic())
                .birthday(localDateToDate(dto.getBirthday()))
                .comment(dto.getComment())
                .examinations(new TreeSet<>())
                .build();

        if (icd != null) {
            if (!icd.getPatients().contains(patient)) {
                icd.addPatient(patient);
            } else {
                patient.setIcd(icd);
            }
        }

        return patient;
    }
}
