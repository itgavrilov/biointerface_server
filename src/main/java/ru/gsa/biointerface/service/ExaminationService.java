package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.SampleRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ExaminationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationService.class);
    private final ExaminationRepository repository;
    private final ChannelRepository channelRepository;
    private final SampleRepository sampleRepository;

    @Autowired
    private ExaminationService(
            ExaminationRepository repository,
            ChannelRepository channelRepository,
            SampleRepository sampleRepository
    ) {
        this.repository = repository;
        this.channelRepository = channelRepository;
        this.sampleRepository = sampleRepository;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("ExaminationService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("ExaminationService is destruction");
    }

    public List<Examination> getAll() throws Exception {
        List<Examination> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations from database");
        } else {
            LOGGER.info("Examinations is not found in database");
        }

        return entities;
    }

    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws Exception {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");

        List<Examination> entities = repository.findAllByPatientRecord(patientRecord);

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations by patientRecord(id={}) from database", patientRecord.getId());
        } else {
            LOGGER.info("Examinations by patientRecord(id={}) is not found in database", patientRecord.getId());
        }

        return entities;
    }

    public Examination getById(Long id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Examination entity = repository.getById(id);

        if (entity != null) {
            LOGGER.info("Get examination(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Examination(id={}) is not found in database", id);
            throw new EntityNotFoundException("Examination(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void recordingStart(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.isRecording())
            throw new IllegalArgumentException("Entity is already being recorded");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");

        Examination readEntity = repository.getById(entity.getId());

        if (readEntity == null) {
            repository.transactionOpen();
            LOGGER.info("Transaction started");
            repository.insert(entity);
            LOGGER.info("Examination(id={}) is recorded", entity.getId());
            entity.recordingStart();
        } else {
            LOGGER.error(
                    "Examination(id={}) already exists in database. Recording is not start",
                    entity.getId());
            throw new IllegalArgumentException(
                    "Examination(id=" + entity.getId() + ") already exists in database. Recording is not start"
            );
        }
    }

    public void recordingStop(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        entity.recordingStop();
        repository.transactionClose();
    }

    public void delete(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Examination readEntity = repository.getById(entity.getId());

        if (readEntity != null) {
            repository.delete(entity);
            LOGGER.info("Examination(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("Examination(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "Examination(id=" + entity.getId() + ") is not found in database"
            );
        }
    }

    public void update(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getStartTime() == null)
            throw new NullPointerException("StartTime is null");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");


        Examination readEntity = repository.getById(entity.getId());

        if (readEntity != null) {
            repository.save(entity);
            LOGGER.info("Examination(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Examination(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Examination(id=" + entity.getId() + ") not found in database");
        }
    }

    public Examination loadWithGraphsById(Long id) throws Exception {
        Examination entity = getById(id);
        entity.setChannels(channelRepository.findAllByExamination(entity));

        for (Channel channel : entity.getChannels()) {
            channel.setSamples(sampleRepository.findAllByChannel(channel));
        }

        LOGGER.info("Examination(id={}) load with graphs from database", entity.getId());

        return entity;
    }
}
