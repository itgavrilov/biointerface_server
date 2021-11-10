package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.ExaminationRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Service
public class ExaminationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationService.class);
    private final ExaminationRepository repository;
    private final SampleService sampleService;
    private final ChannelService channelService;

    @Autowired
    public ExaminationService(
            ExaminationRepository repository,
            ChannelService channelService,
            SampleService sampleService
    ) {
        this.repository = repository;
        this.channelService = channelService;
        this.sampleService = sampleService;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("ExaminationService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("ExaminationService is destruction");
    }

    public List<Examination> findAll() throws Exception {
        List<Examination> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations from database");
        } else {
            LOGGER.info("Examinations is not found in database");
        }

        return entities;
    }

    public List<Examination> getByPatientRecord(Patient patient) throws Exception {
        if (patient == null)
            throw new NullPointerException("PatientRecord is null");

        List<Examination> entities = repository.findAllByPatient(patient);

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations by patientRecord(id={}) from database", patient.getId());
        } else {
            LOGGER.info("Examinations by patientRecord(id={}) is not found in database", patient.getId());
        }

        return entities;
    }

    public Examination findById(Integer id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Examination> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get examination(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("Examination(id={}) is not found in database", id);
            throw new EntityNotFoundException("Examination(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Examination save(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getStarttime() == null)
            throw new NullPointerException("StartTime is null");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        entity = repository.save(entity);
        LOGGER.info("Examination(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Examination> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(entity);
            LOGGER.info("Examination(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("Examination(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "Examination(id=" + entity.getId() + ") is not found in database"
            );
        }
    }

    public Examination loadWithGraphsById(int id) throws Exception {
        Examination entity = findById(id);
        entity.setChannels(channelService.findAllByExamination(entity));

        for (Channel channel : entity.getChannels()) {
            channel.setSamples(sampleService.findAllByChannel(channel));
        }

        LOGGER.info("Examination(id={}) load with channels from database", entity.getId());

        return entity;
    }

    public void recordingStart(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");

        Optional<Examination> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            sampleService.transactionOpen();
            LOGGER.info("Recording started");
        } else {
            LOGGER.error(
                    "Examination(id={}) does not yet exist in database. Recording is not start",
                    entity.getId());
            throw new NullPointerException(
                    "Examination(id=" + entity.getId() + ") does not yet exist in database. Recording is not start"
            );
        }
    }

    public void recordingStop() throws Exception {
        sampleService.transactionClose();
        LOGGER.info("Recording stopped");
    }

    public boolean isRecording() {
        return sampleService.transactionIsOpen();
    }
}
