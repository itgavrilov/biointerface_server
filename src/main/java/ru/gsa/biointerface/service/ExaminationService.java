package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.ExaminationRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class ExaminationService {
    @Autowired
    private ExaminationRepository repository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private SampleService sampleService;

    @PostConstruct
    private void init() {
        log.info("ExaminationService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("ExaminationService is destruction");
    }

    public Set<Examination> findAll() {
        Set<Examination> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all examinations from database");
        } else {
            log.info("Examinations is not found in database");
        }

        return entities;
    }

    public Set<Examination> findByPatient(Patient patient) {
        if (patient == null)
            throw new NullPointerException("Patient is null");

        Set<Examination> entities = new TreeSet<>(repository.findAllByPatient(patient));

        if (entities.size() > 0) {
            log.info("Get all examinations by patient(id={}) from database", patient.getId());
        } else {
            log.info("Examinations by patient(id={}) is not found in database", patient.getId());
        }

        return entities;
    }

    public Set<Examination> findByDevice(Device device) {
        if (device == null)
            throw new NullPointerException("Device is null");

        Set<Examination> entities = new TreeSet<>(repository.findAllByDevice(device));

        if (entities.size() > 0) {
            log.info("Get all examinations by device(id={}) from database", device.getId());
        } else {
            log.info("Examinations by device(id={}) is not found in database", device.getId());
        }

        return entities;
    }

    public Examination findById(Integer id) {
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
    public Examination save(Examination entity) {
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
    public void delete(Examination entity) {
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

    public Examination loadWithGraphsById(int id) {
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

    public ExaminationDTO convertEntityToDto(Examination entity) {
        int patient_id = 0;
        int device_id = 0;

        if (entity.getPatient() != null) {
            patient_id = entity.getPatient().getId();
        }

        if (entity.getDevice() != null) {
            device_id = entity.getDevice().getId();
        }

        return ExaminationDTO.builder()
                .id(entity.getId())
                .starttime(LocalDateTime.ofInstant(
                        entity.getStarttime().toInstant(),
                        ZoneId.systemDefault())
                )
                .patientId(patient_id)
                .deviceId(device_id)
                .comment(entity.getComment())
                .build();
    }

    public Examination convertDtoToEntity(ExaminationDTO dto) {
        Patient patient = null;
        Device device = null;

        if (dto.getPatientId() != 0) {
            patient = patientService.findById(dto.getPatientId());
        }

        if (dto.getDeviceId() != 0) {
            device = deviceService.findById(dto.getDeviceId());
        }

        Examination examination = Examination.builder()
                .id(dto.getId())
                .starttime(Timestamp.valueOf(dto.getStarttime()))
                .comment(dto.getComment())
                .channels(new ArrayList<>())
                .build();

        if (patient != null) {
            if (!patient.getExaminations().contains(examination)) {
                patient.addExamination(examination);
            } else {
                examination.setPatient(patient);
            }
        }

        if (device != null) {
            if (!device.getExaminations().contains(examination)) {
                device.addExamination(examination);
            } else {
                examination.setDevice(device);
            }
        }

        return examination;
    }
}
