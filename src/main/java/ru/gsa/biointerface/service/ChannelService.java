package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

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
        ChannelName channelName = channelNameService.findById(channelNameId);
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

    public ChannelDTO convertEntityToDto(Channel entity) {
        int channelName_id = 0;

        if (entity.getChannelName() != null)
            channelName_id = entity.getChannelName().getId();

        return ChannelDTO.builder()
                .number(entity.getId().getNumber())
                .examinationId(
                        entity.getId().getExamination_id()
                )
                .channelNameId(channelName_id)
                .build();
    }

    public Channel convertDtoToEntity(ChannelDTO dto) {
        Examination examination = null;
        int examination_id = 0;
        ChannelName channelName = null;

        if (dto.getExaminationId() != 0) {
            examination = examinationService.getById(dto.getExaminationId());
            examination_id = examination.getId();
        }

        if (dto.getChannelNameId() != 0) {
            channelName = channelNameService.findById(dto.getChannelNameId());
        }

        Channel channel = Channel.builder()
                .id(new ChannelID(dto.getNumber(), examination_id))
                .samples(new ArrayList<>())
                .build();

        if (examination != null) {
            if (!examination.getChannels().contains(channel)) {
                examination.addChannel(channel);
            } else {
                channel.setExamination(examination);
            }
        }

        if (channelName != null) {
            if (!channelName.getChannels().contains(channel)) {
                channelName.addChannel(channel);
            } else {
                channel.setChannelName(channelName);
            }
        }

        return channel;
    }
}
