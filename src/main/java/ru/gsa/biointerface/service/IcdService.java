package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
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

    public Icd getById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Icd> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get icd(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("Icd(id={}) is not found in database", id);
            throw new NotFoundException("Icd(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Icd save(IcdDTO dto) {
        Optional<Icd> optional = repository.findById(dto.getId());
        Icd entity;

        if(optional.isEmpty()){
            entity = new Icd(dto.getName(), dto.getVersion(), dto.getComment());
        } else {
            entity = optional.get();
            entity.setName(dto.getName());
            entity.setVersion(dto.getVersion());
            entity.setComment(dto.getComment());
        }

        entity = repository.save(entity);
        log.info("Icd(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int id) {
        if (id <= 0) throw new BadRequestException("Id <= 0");

        Optional<Icd> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Icd(id={}) is deleted in database", id);
        } else {
            log.info("Icd(id={}) not found in database", id);
            throw new NotFoundException("Icd(id=" + id + ") not found in database");
        }
    }
}
