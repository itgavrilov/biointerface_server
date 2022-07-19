package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.repository.ExaminationRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    private final ExaminationRepository examinationRepository;

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
     * Получение списка каналов с пагинацией
     *
     * @param examinationId Идентификатор исследования(необязательный) {@link Examination#getId()}
     * @param channelNameId Идентификатор наименования канала
     *                      контроллера биоинтерфейса(необязательный) {@link ChannelName#getId()}
     * @param pageable      Пагинация {@link Pageable}
     * @return Список каналов {@link Page<Channel>}
     */
    public Page<Channel> findAll(Integer examinationId, Integer channelNameId, Pageable pageable) {
        return repository.findAllByExaminationIdAndChannelNameId(examinationId, channelNameId, pageable);
    }

    /**
     * Получение канала по id
     *
     * @param examinationId Идентификатор исследования {@link Examination#getId()}
     * @param number        Номер канала {@link Channel#getId()}
     * @return Канал {@link Channel}
     * @throws NotFoundException если канала с id не найдено
     */
    public Channel findById(Integer examinationId, Integer number) {
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
        Optional<Channel> optional = repository.getByNumberAndExaminationId(dto.getExaminationId(), dto.getNumber());
        Channel entity;

        if (optional.isEmpty()) {
            entity = new Channel(dto.getNumber(),
                    examinationRepository.getOrThrow(dto.getExaminationId()),
                    channelNameRepository.getOrThrow(dto.getChannelNameId()));
        } else {
            entity = optional.get();

            if (entity.getExamination().getId().equals(dto.getExaminationId())) {
                entity.setExamination(examinationRepository.getOrThrow(dto.getExaminationId()));
            }

            if (entity.getChannelName().getId().equals(dto.getChannelNameId())) {
                entity.setChannelName(channelNameRepository.getOrThrow(dto.getChannelNameId()));
            }
        }

        entity = update(entity);
        log.debug("Channel(id={}) is save", entity.getId());

        return entity;
    }

    @Transactional
    public Channel update(Channel entity) {
        entity = repository.save(entity);
        log.info("Channel(id={})  is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int examinationId, int number) {
        ChannelID id = new ChannelID(examinationId, number);
        Optional<Channel> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Channel(id={}) is deleted in database", optional.get().getId());
        } else {
            log.info("Channel(id={}) not found in database", id);
            throw new NotFoundException("Channel(id=" + id + ") not found in database");
        }
    }
}
