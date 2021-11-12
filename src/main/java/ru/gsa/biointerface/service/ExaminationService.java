package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ExaminationService {
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
        log.info("ExaminationService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("ExaminationService is destruction");
    }

    public List<Examination> findAll() throws Exception {
        List<Examination> entities = repository.findAll();

        if (entities.size() > 0) {
            log.info("Get all examinations from database");
        } else {
            log.info("Examinations is not found in database");
        }

        return entities;
    }

    public List<Examination> getByPatientRecord(Patient patient) throws Exception {
        if (patient == null)
            throw new NullPointerException("PatientRecord is null");

        List<Examination> entities = repository.findAllByPatient(patient);

        if (entities.size() > 0) {
            log.info("Get all examinations by patientRecord(id={}) from database", patient.getId());
        } else {
            log.info("Examinations by patientRecord(id={}) is not found in database", patient.getId());
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
            log.info("Get examination(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("Examination(id={}) is not found in database", id);
            throw new EntityNotFoundException("Examination(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Examination save(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getStarttime() == null)
            throw new NullPointerException("StartTime is null");
        if (entity.getPatient() == null)
            throw new NullPointerException("Patient is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        entity = repository.save(entity);
        log.info("Examination(id={}) is recorded in database", entity.getId());

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
            log.info("Examination(id={}) is deleted in database", entity.getId());
        } else {
            log.info("Examination(id={}) not found in database", entity.getId());
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

        log.info("Examination(id={}) load with channels from database", entity.getId());

        return entity;
    }

    public void recordingStart(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getPatient() == null)
            throw new NullPointerException("Patient is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");

        Optional<Examination> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            sampleService.transactionOpen();
            log.info("Recording started");
        } else {
            log.error(
                    "Examination(id={}) does not yet exist in database. Recording is not start",
                    entity.getId());
            throw new NullPointerException(
                    "Examination(id=" + entity.getId() + ") does not yet exist in database. Recording is not start"
            );
        }
    }

    public void recordingStop() throws Exception {
        sampleService.transactionClose();
        log.info("Recording stopped");
    }

    public boolean isRecording() {
        return sampleService.transactionIsOpen();
    }
}
