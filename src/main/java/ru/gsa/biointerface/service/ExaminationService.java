package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ExaminationRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cервис для работы с исследованиями
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExaminationService {

    private final ExaminationRepository repository;
    private final PatientService patientService;
    private final DeviceService deviceService;
    private final SampleService sampleService;

    @PostConstruct
    private void init() {
        log.debug("ExaminationService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("ExaminationService is destruction");
    }

    /**
     * Получение списка исследований
     *
     * @param patientId Идентификатор карточки пациента(необязательный) {@link Patient#getId()}
     * @param deviceId  Идентификатор контроллера биоинтерфейса(необязательный) {@link Device#getId()}
     * @return Список исследований {@link List<Examination>}
     */
    public List<Examination> findAll(UUID patientId, UUID deviceId) {
        return repository.findAllByPatientIdAndDeviceId(patientId, deviceId);
    }

    /**
     * Получение списка исследований с пагинацией
     *
     * @param patientId Идентификатор карточки пациента(необязательный) {@link Patient#getId()}
     * @param deviceId  Идентификатор контроллера биоинтерфейса(необязательный) {@link Device#getId()}
     * @param pageable  Пагинация {@link Pageable}
     * @return Список исследований с пагинацией {@link Page<Examination>}
     */
    public Page<Examination> findAll(UUID patientId, UUID deviceId, Pageable pageable) {
        return repository.findAllByPatientIdAndDeviceId(patientId, deviceId, pageable);
    }

    /**
     * Получение исследования по id
     *
     * @param id Идентификатор {@link Examination#getId()}
     * @return Исследование {@link Examination}
     * @throws NotFoundException если исследования с id не найдено
     */
    public Examination getById(UUID id) {
        return repository.getOrThrow(id);
    }

    /**
     * Создание исследования
     *
     * @param request Сущность исследования {@link Examination}
     * @return Карточка пациента {@link Patient}
     */
    public Examination save(Examination request) {
        if (repository.existsByPatientIdAndDeviceIdAndDatetime(
                request.getPatientId(),
                request.getDeviceId(),
                request.getDatetime())) {
            throw new BadRequestException(String.format(
                    "Examination(patientId=%s, deviceId=%s, datetime=%s) already exists",
                    request.getPatientId(), request.getDeviceId(), request.getDatetime()));
        }

        patientService.getById(request.getPatientId());
        deviceService.getById(request.getDeviceId());
        Examination entity = repository.save(request);
        log.info("Examination(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Создание/обновление исследования
     *
     * @param request Сущность исследования {@link Examination}
     * @return Карточка пациента {@link Patient}
     */
    @Transactional
    public Examination update(Examination request) {
        patientService.getById(request.getPatientId());

        Examination entity = repository.getOrThrow(request.getId());
        entity.setPatientId(request.getPatientId());
        entity.setComment(request.getComment());
        log.info("Examination(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Удаление исследования
     *
     * @param id Идентификатор {@link Examination#getId()}
     * @throws NotFoundException если исследования с id не найдено
     */
    @Transactional
    public void delete(UUID id) {
        Examination entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.info("Examination(id={}) is deleted", id);
    }

    @Transactional
    public Examination loadWithGraphsById(UUID id) {
        Examination entity = getById(id);
        if (entity.getChannels() == null) {
            throw new NotFoundException(String.format(
                    "No graph is found for Examination(id=%s).",
                    entity.getId()));
        }
        entity.getChannels().forEach(channel -> {
        });

        log.info("Examination(id={}) load with channels from database", entity.getId());

        return entity;
    }

    public void recordingStart(@Valid Examination entity) throws Exception {

        Optional<Examination> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            sampleService.transactionOpen();
            log.info("Recording started");
        } else {
            log.error("Examination(id={}) does not yet exist in database. Recording is not start",
                    entity.getId());
            throw new NotFoundException(String.format(
                    "Examination(id=%s) does not yet exist in database. Recording is not start",
                    entity.getId()));
        }
    }

    public void recordingStop() throws Exception {
        sampleService.transactionClose();
        log.info("Recording stopped");
    }

    public boolean isRecording() {
        return sampleService.transactionIsOpen();
    }
}
