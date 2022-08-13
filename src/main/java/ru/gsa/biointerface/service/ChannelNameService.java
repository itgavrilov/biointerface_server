package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;

/**
 * CRUD-сервис для работы с наименованиями каналаов контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelNameService {
    private final ChannelNameRepository repository;

    @PostConstruct
    private void init() {
        log.debug("ChannelNameService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("ChannelNameService is destruction");
    }

    /**
     * Получение списка наименований
     *
     * @return Список наименований {@link List<ChannelName>}
     */
    public List<ChannelName> findAll() {
        return repository.findAll();
    }

    /**
     * Получение списка наименований с пагинацией
     *
     * @param pageable Пагинация {@link Pageable}
     * @return Список наименований с пагинацией {@link Page<ChannelName>}
     */
    public Page<ChannelName> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Получение наименования по id
     *
     * @param id Идентификатор {@link ChannelName#getId()}
     * @return Наименование {@link ChannelName}
     * @throws NotFoundException если наименование с id не найдено
     */
    public ChannelName getById(UUID id) {
        return repository.getOrThrow(id);
    }

    /**
     * Получение наименования по id
     *
     * @param id Идентификатор {@link ChannelName#getId()}
     * @return Наименование {@link ChannelName}
     * @throws NotFoundException если наименование с id не найдено
     */
    public ChannelName getByIdOrNull(UUID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Создание наименования
     *
     * @param request Новое наименование {@link ChannelName}
     * @return Наименование {@link ChannelName}
     */
    public ChannelName save(ChannelName request) {
        if(repository.existsByName(request.getName())){
            throw new BadRequestException(String.format("ChannelName(name=%s) already exists", request.getName()));
        }

        request.setId(null);
        ChannelName entity = repository.save(request);
        log.info("ChannelName(id={}) is save", entity.getId());

        return repository.getOrThrow(entity.getId());
    }

    /**
     * Обновление наименования
     *
     * @param request наименование канала {@link ChannelName}
     * @return Наименование {@link ChannelName}
     */
    public ChannelName update(ChannelName request) {
        ChannelName entity = repository.getOrThrow(request.getId());

        entity.setName(request.getName());
        entity.setComment(request.getComment());
        repository.save(entity);
        log.info("ChannelName(id={}) is update", entity.getId());

        return repository.getOrThrow(entity.getId());
    }

    /**
     * Удаление наименования
     *
     * @param id Идентификатор {@link ChannelName#getId()}
     * @throws NotFoundException если наименование с id не найдено
     */
    public void delete(UUID id) {
        ChannelName entity = repository.getOrThrow(id);
        repository.delete(entity);
        log.info("ChannelName(id={}) is deleted", id);
    }
}
