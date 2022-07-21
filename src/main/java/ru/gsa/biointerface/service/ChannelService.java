package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.dto.ChannelDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.ChannelRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;

/**
 * CRUD-сервис для работы с каналами контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {

    private final ChannelRepository repository;
    private final ChannelNameRepository channelNameRepository;

    @PostConstruct
    private void init() {
        log.debug("ChannelService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("ChannelService is destruction");
    }

    /**
     * Получение списка каналов
     *
     * @param examinationId Идентификатор исследования(необязательный) {@link Examination#getId()}
     * @param channelNameId Идентификатор наименования канала
     *                      контроллера биоинтерфейса(необязательный) {@link ChannelName#getId()}
     * @return Список каналов {@link List<Channel>}
     */
    public List<Channel> findAll(Integer examinationId, Integer channelNameId) {
        return repository.findAllByExaminationIdAndChannelNameId(examinationId, channelNameId);
    }

    /**
     * Получение канала по id
     *
     * @param examinationId Идентификатор исследования {@link Examination#getId()}
     * @param number        Номер канала {@link Channel#getId()}
     * @return Канал {@link Channel}
     * @throws NotFoundException если канала с id не найдено
     */
    public Channel getById(Integer examinationId, Integer number) {
        return repository.getOrThrow(examinationId, number);
    }

    /**
     * Обновление канала
     *
     * @param dto DTO канала {@link ChannelDTO}
     * @return Канал {@link Channel}
     */
    @Transactional
    public Channel update(ChannelDTO dto) {
        Channel entity = repository.getOrThrow(dto.getExaminationId(), dto.getNumber());

        if (!entity.getChannelName().getId().equals(dto.getChannelNameId())) {
            entity.setChannelName(channelNameRepository.getOrThrow(dto.getChannelNameId()));
        }

        log.info("Channel(id={}) is update", entity.getId());

        return entity;
    }

    /**
     * Сохранение нового канала
     *
     * @param entity Новый канал {@link Channel}
     * @return Канал {@link Channel}
     */
    public Channel save(Channel entity) {
        entity = repository.save(entity);
        log.info("Channel(id={}) is save", entity.getId());

        return entity;
    }

    /**
     * Удаление канала
     *
     * @param examinationId Идентификатор исследования {@link Examination#getId()}
     * @param number        Номер канала {@link ChannelID#getNumber()}
     * @throws NotFoundException если устройстройсвто с id не найдено
     */
    public void delete(Integer examinationId, Integer number) {
        Channel entity = repository.getOrThrow(examinationId, number);
        repository.delete(entity);
        log.debug("Channel(examinationId={}, number={}) is deleted", examinationId, number);
    }
}
