package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class IcdService {
    private final IcdRepository repository;

    @Autowired
    public IcdService(IcdRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        log.info("IcdService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("IcdService is destruction");
    }

    public Set<Icd> findAll() {
        Set<Icd> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all icds from database");
        } else {
            log.info("Icds is not found in database");
        }

        return entities;
    }

    public Icd findById(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Icd> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get icd(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("Icd(id={}) is not found in database", id);
            throw new NoSuchElementException("Icd(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Icd save(Icd entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("Version <= 0");
        if (entity.getPatients() == null)
            throw new NullPointerException("Patients is null");


        entity = repository.save(entity);
        log.info("Icd(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(Icd entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Icd> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Icd(id={}) is deleted in database", optional.get().getId());
        } else {
            log.info("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }

    public IcdDTO convertEntityToDto(Icd entity) {
        return IcdDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .version(entity.getVersion())
                .comment(entity.getComment())
                .build();
    }

    public Icd convertDtoToEntity(IcdDTO dto) {
        return Icd.builder()
                .id(dto.getId())
                .name(dto.getName())
                .version(dto.getVersion())
                .comment(dto.getComment())
                .patients(new TreeSet<>())
                .build();
    }
}
