package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.DeviceDTO;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * CRUD-сервис для работы с контроллерами биоинтерфейсов
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository repository;

    @PostConstruct
    private void init() {
        log.debug("DeviceService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("DeviceService is destruction");
    }

    /**
     * Получение списка устройств
     *
     * @return Список устройств {@link List<Device>}
     */
    public List<Device> findAll() {
        return repository.findAll();
    }

    /**
     * Получение списка устройств с пагинацией
     *
     * @param pageable Пагинация {@link Pageable}
     * @return Список устройств с пагинацией {@link Page<Device>}
     */
    public Page<Device> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Получение контроллера биоинтерфейса по id
     *
     * @param id Идентификатор {@link Device#getId()}
     * @return Контроллер биоинтерфейса {@link Device}
     * @throws NotFoundException если устройстройсвто с id не найдено
     */
    public Device getById(UUID id) {
        return repository.getOrThrow(id);
    }

    /**
     * Сохранение контроллера биоинтерфейса
     *
     * @param entity DTO контроллера биоинтерфейса {@link DeviceDTO}
     * @return Контроллер биоинтерфейса {@link Device}
     */
    public Device save(Device entity) {
        entity = repository.save(entity);
        log.info("Device(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Создание/обновление контроллера биоинтерфейса
     *
     * @param request Контроллер биоинтерфейса {@link Device}
     * @return Контроллер биоинтерфейса {@link Device}
     */
    @Transactional
    public Device update(Device request) {
        Device entity = repository.getOrThrow(request.getId());

        entity.setAmountChannels(request.getAmountChannels());
        entity.setComment(request.getComment());

        log.info("Device(id={}) is update", entity.getId());

        return entity;
    }

    /**
     * Удаление контроллера биоинтерфейса
     *
     * @param id Идентификатор {@link Device#getId()}
     * @throws NotFoundException если устройстройсвто с id не найдено
     */
    public void delete(UUID id) {
        Device entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.info("Device(id={}) is deleted", id);
    }
}
