package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;

/**
 * CRUD-сервис для работы с заболеваниями по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IcdService {
    private final IcdRepository repository;

    @PostConstruct
    private void init() {
        log.debug("IcdService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("IcdService is destruction");
    }

    /**
     * Получение списка заболиваний
     *
     * @return Список заболиваний {@link List<Icd>}
     */
    public List<Icd> findAll() {
        return repository.findAll();
    }

    /**
     * Получение списка заболиваний с пагинацией
     *
     * @param pageable Пагинация {@link Pageable}
     * @return Список устройств с пагинацией {@link Page<Icd>}
     */
    public Page<Icd> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Получение заболиваний по id
     *
     * @param id Идентификатор {@link Icd#getId()}
     * @return Заболивание {@link Icd}
     * @throws NotFoundException если заболивание с id не найдено
     */
    public Icd getById(UUID id) {
        return repository.getOrThrow(id);
    }

    /**
     * Получение заболиваний по id
     * Если не найдено то вернет null
     *
     * @param id Идентификатор {@link Icd#getId()}
     * @return Заболивание {@link Icd}
     */
    public Icd getByIdOrNull(UUID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Создание заболивания
     *
     * @param request Заболивание {@link Icd}
     * @return Заболивание {@link Icd}
     */
    public Icd save(Icd request) {
        if (repository.existsByNameAndVersion(request.getName(), request.getVersion())) {
            throw new BadRequestException(String.format("ChannelName(name=%s, version=%s) already exists",
                    request.getName(), request.getVersion()));
        }

        request.setId(null);
        Icd entity = repository.save(request);
        log.info("Icd(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Обновление заболивания
     *
     * @param request Заболивание {@link Icd}
     * @return Заболивание {@link Icd}
     */
    @Transactional
    public Icd update(Icd request) {
        Icd entity = repository.getOrThrow(request.getId());

        entity.setName(request.getName());
        entity.setVersion(request.getVersion());
        entity.setComment(request.getComment());
        log.info("Icd(id={}) is update", entity.getId());

        return entity;
    }

    /**
     * Удаление заболивания
     *
     * @param id Идентификатор {@link Icd#getId()}
     * @throws NotFoundException если заболивание с id не найдено
     */
    public void delete(UUID id) {
        Icd entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.info("Icd(id={}) is deleted", id);
    }
}
