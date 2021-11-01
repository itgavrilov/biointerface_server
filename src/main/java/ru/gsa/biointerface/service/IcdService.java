package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class IcdService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcdService.class);
    private final IcdRepository dao;

    @Autowired
    private IcdService(IcdRepository dao) {
        this.dao = dao;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("IcdService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("IcdService is destruction");
    }

    public List<Icd> getAll() throws Exception {
        List<Icd> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all icds from database");
        } else {
            LOGGER.info("Icds is not found in database");
        }

        return entities;
    }

    public Icd getById(Long id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Icd entity = dao.getById(id);

        if (entity != null) {
            LOGGER.info("Get icd(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) is not found in database", id);
            throw new NoSuchElementException("Icd(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(Icd entity) throws Exception {
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

        Icd readEntity = dao.getById(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("Icd(id={}) is recorded in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) already exists in database", entity.getId());
            throw new IllegalArgumentException("Icd(id=" + entity.getId() + ") already exists in database");
        }
    }

    public void delete(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Icd readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
            LOGGER.info("Icd(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("Version <= 0");
        if (entity.getPatientRecords() == null)
            throw new NullPointerException("PatientRecords is null");

        Icd readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("Icd(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }
}
