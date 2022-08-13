package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
     * Создание карточки пациента
     *
     * @param request Карточка пациента {@link Patient}
     * @return Карточка пациента {@link Patient}
     */
    public Patient save(Patient request) {
        if(repository.existsBySecondNameAndFirstNameAndPatronymicAndBirthday(request.getSecondName(),
                request.getFirstName(), request.getPatronymic(), request.getBirthday())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            throw new BadRequestException(String.format(
                    "ChannelName(second name=%s, first name=%s, patronymic=%s, birthday=%s) already exists",
                    request.getSecondName(), request.getFirstName(), request.getPatronymic(),
                    formatter.format(request.getBirthday())));
        }

        request.setId(null);
        Patient entity = repository.save(request);
        log.info("Patient(id={}) is save", entity.getId());

        return repository.getOrThrow(entity.getId());
    }

    /**
     * Обновление карточки пациента
     *
     * @param request Карточка пациента {@link Patient}
     * @return Карточка пациента {@link Patient}
     */
    public Patient update(Patient request) {
        Patient entity = repository.getOrThrow(request.getId());
        entity.setSecondName(request.getSecondName());
        entity.setFirstName(request.getFirstName());
        entity.setPatronymic(request.getPatronymic());
        entity.setBirthday(request.getBirthday());
        entity.setIcd(request.getIcd());
        entity.setComment(request.getComment());
        repository.save(entity);
        log.info("Patient(id={}) is save", entity.getId());

        return repository.getOrThrow(entity.getId());
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
