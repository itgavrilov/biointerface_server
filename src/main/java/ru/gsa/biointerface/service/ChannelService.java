package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.ChannelID;
import ru.gsa.biointerface.domain.ChannelName;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ChannelRepository repository;
    private final ExaminationService examinationService;
    private final ChannelNameService channelNameService;

    @PostConstruct
    private void init() {
        log.info("ChannelService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("ChannelService is destruction");
    }

    public Set<Channel> findAll() {
        Set<Channel> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all channels from database");
        } else {
            log.info("Channels is not found in database");
        }

        return entities;
    }

    public List<Channel> findAllByExamination(int id) {
        Examination examination = examinationService.getById(id);
        List<Channel> entities = repository.findAllByExamination(examination);

        if (entities.size() > 0) {
            log.info("Get all channels by examination from database");
        } else {
            log.info("Channels by examination is not found in database");
        }

        return entities;
    }

    public Set<Channel> findAllByChannelName(int channelNameId) {
        ChannelName channelName = channelNameService.getById(channelNameId);
        Set<Channel> entities = new TreeSet<>(
                repository.findAllByChannelName(channelName));

        if (entities.size() > 0) {
            log.info("Get all channels by channelName from database");
        } else {
            log.info("Channels by channelName is not found in database");
        }

        return entities;
    }

    public Channel findById(int examinationId, int number) {
        ChannelID id = new ChannelID(examinationId, number);
        Optional<Channel> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get channel(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("Channel(id={}) is not found in database", id);
            throw new NotFoundException("Channel(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Channel save(Channel entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getExamination() == null)
            throw new NullPointerException("Examination is null");
        if (entity.getSamples() == null)
            throw new NullPointerException("Samples is null");

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
