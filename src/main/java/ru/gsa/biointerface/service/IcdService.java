package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Service
public class IcdService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcdService.class);
    private final IcdRepository repository;

    @Autowired
    public IcdService(IcdRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("IcdService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("IcdService is destruction");
    }

    public List<Icd> findAll() throws Exception {
        List<Icd> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all icds from database");
        } else {
            LOGGER.info("Icds is not found in database");
        }

        return entities;
    }

    public Icd findById(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Icd> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get icd(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("Icd(id={}) is not found in database", id);
            throw new NoSuchElementException("Icd(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Icd save(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("Version <= 0");
        if (entity.getPatientRecords() == null)
            throw new NullPointerException("PatientRecords is null");


        entity = repository.save(entity);
        LOGGER.info("Icd(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Icd> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("Icd(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.info("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }
}
