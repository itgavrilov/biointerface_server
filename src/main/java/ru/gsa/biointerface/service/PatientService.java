package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CRUD-сервис для работы с карточками пациентов
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final IcdService icdService;

    @PostConstruct
    private void init() {
        log.debug("PatientService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("PatientService is destruction");
    }

    /**
     * Получение списка карточек пациентов c учетом идентификатора заболивания
     *
     * @param icdId Идентификатор заболивания(необязательный) {@link Icd#getId()}
     * @return Список карточек пациентов {@link List<Patient>}
     */
    public List<Patient> findAll(UUID icdId) {
        return repository.findAllByIcd(icdId);
    }

    /**
     * Получение списка карточек пациентов c учетом идентификатора заболивания с пагинацией
     *
     * @param icdId    Идентификатор заболивания(необязательный) {@link Icd#getId()}
     * @param pageable Пагинация {@link Pageable}
     * @return Список карточек пациентов с пагинацией {@link Page<Patient>}
     */
    public Page<Patient> findAll(UUID icdId, Pageable pageable) {
        return repository.findAllByIcd(icdId, pageable);
    }

    /**
     * Получение карточки пациента по id
     *
     * @param id Идентификатор {@link Patient#getId()}
     * @return Карточка пациента {@link Patient}
     * @throws NotFoundException если карточки пациента с id не найдено
     */
    public Patient getById(UUID id) {
        return repository.getOrThrow(id);
    }

    /**
     * Создание/обновление карточки пациента
     *
     * @param dto DTO карточки пациента {@link PatientDTO}
     * @return Карточка пациента {@link Patient}
     */
    public Patient saveOrUpdate(@Validated PatientDTO dto) {
        Optional<Patient> optional;
        Patient entity;
        Icd icd = null;

        if(dto.getIcdId() != null) {
            icd = icdService.getById(dto.getIcdId());
        }

        if(dto.getId() != null){
            optional = repository.findById(dto.getId());
        } else {
            optional = Optional.empty();
        }

        if (optional.isEmpty()) {
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
        log.info("Patient(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Удаление карточки пациента
     *
     * @param id Идентификатор {@link Patient#getId()}
     * @throws NotFoundException если карточки пациента с id не найдено
     */
    public void delete(UUID id) {
        Patient entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.info("Patient(id={}) is deleted", id);
    }
}
