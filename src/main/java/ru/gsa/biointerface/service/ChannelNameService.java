package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class ChannelNameService {
    private final ChannelNameRepository repository;

    @Autowired
    public ChannelNameService(ChannelNameRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        log.info("ChannelNameService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("ChannelNameService is destruction");
    }

    public Set<ChannelName> findAll() {
        Set<ChannelName> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all channelNames from database");
        } else {
            log.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName findById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get channelName(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("ChannelName(id={}) is not found in database", id);
            throw new NotFoundException("ChannelName(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public ChannelName save(ChannelName entity) {
        if (entity == null)
            throw new BadRequestException("Entity is null");
        if (entity.getName() == null)
            throw new BadRequestException("Name is null");
        if (entity.getName().isBlank())
            throw new BadRequestException("Name is blank");
        if (entity.getChannels() == null)
            throw new BadRequestException("Channels is null");

        entity = repository.save(entity);
        log.info("ChannelName(id={})  is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("ChannelName(id={}) is deleted in database", optional.get().getId());
        } else {
            log.info("ChannelName(id={}) not found in database", id);
            throw new NotFoundException("ChannelName(id=" + id + ") not found in database");
        }
    }

    public ChannelNameDTO convertEntityToDto(ChannelName entity) {
        return ChannelNameDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .comment(entity.getComment())
                .build();
    }

    public ChannelName convertDtoToEntity(ChannelNameDTO dto) {
        return ChannelName.builder()
                .id(dto.getId())
                .name(dto.getName())
                .comment(dto.getComment())
                .channels(new TreeSet<>())
                .build();
    }
}
