package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ExaminationRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class ExaminationService {
    private final ExaminationRepository repository;
    private final PatientService patientService;
    private final DeviceService deviceService;
    private final ChannelService channelService;
    private final SampleService sampleService;

    @Autowired
    public ExaminationService(ExaminationRepository repository,
                              @Lazy PatientService patientService,
                              @Lazy DeviceService deviceService,
                              @Lazy ChannelService channelService,
                              @Lazy SampleService sampleService) {
        this.repository = repository;
        this.patientService = patientService;
        this.deviceService = deviceService;
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

    public Set<Examination> findAll() {
        Set<Examination> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all examinations from database");
        } else {
            log.info("Examinations is not found in database");
        }

        return entities;
    }

    public Set<Examination> findByPatient(int patientId) {
        if (patientId <= 0) throw new NullPointerException("patientId <= 0");

        Patient patient = patientService.getById(patientId);
        Set<Examination> entities = new TreeSet<>(repository.findAllByPatient(patient));

        if (entities.size() > 0) {
            log.info("Get all examinations by patient(id={}) from database", patientId);
        } else {
            log.info("Examinations by patient(id={}) is not found in database", patientId);
        }

        return entities;
    }

    public Set<Examination> findByDevice(int deviceId) {
        if (deviceId <= 0) throw new NullPointerException("deviceId <= 0");

        Device device = deviceService.getById(deviceId);
        Set<Examination> entities = new TreeSet<>(repository.findAllByDevice(device));

        if (entities.size() > 0) {
            log.info("Get all examinations by device(id={}) from database", device.getId());
        } else {
            log.info("Examinations by device(id={}) is not found in database", device.getId());
        }

        return entities;
    }

    public Examination getById(int id) {
        if (id <= 0) throw new IllegalArgumentException("id <= 0");

        Optional<Examination> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get examination(id={}) from database", id);

            return optional.get();
        } else {
            log.error("Examination(id={}) is not found in database", id);
            throw new NotFoundException("Examination(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Examination save(Examination entity) {
        if (entity == null)
            throw new BadRequestException("Entity is null");
        if (entity.getStarttime() == null)
            throw new BadRequestException("StartTime is null");
        if (entity.getPatient() == null)
            throw new BadRequestException("Patient is null");
        if (entity.getDevice() == null)
            throw new BadRequestException("Device is null");
        if (entity.getChannels() == null)
            throw new BadRequestException("Channels is null");

        entity = repository.save(entity);
        log.info("Examination(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Examination> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Examination(id={}) is deleted in database", id);
        } else {
            log.info("Examination(id={}) not found in database", id);
            throw new NotFoundException("Examination(id=" + id + ") is not found in database");
        }
    }

    public Examination loadWithGraphsById(int id) {
        Examination entity = getById(id);
        entity.setChannels(channelService.findAllByExamination(entity.getId()));

        for (Channel channel : entity.getChannels()) {
            channel.setSamples(sampleService.findAllByChannel(channel));
        }

        log.info("Examination(id={}) load with channels from database", entity.getId());

        return entity;
    }

    public void recordingStart(Examination entity) throws Exception {
        if (entity == null)
            throw new BadRequestException("Entity is null");
        if (entity.getPatient() == null)
            throw new BadRequestException("Patient is null");
        if (entity.getDevice() == null)
            throw new BadRequestException("Device is null");
        if (entity.getChannels() == null)
            throw new BadRequestException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new BadRequestException("Amount channels differs from amount in device");

        Optional<Examination> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            sampleService.transactionOpen();
            log.info("Recording started");
        } else {
            log.error(
                    "Examination(id={}) does not yet exist in database. Recording is not start",
                    entity.getId());
            throw new NotFoundException(
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
            patient = patientService.getById(dto.getPatientId());
        }

        if (dto.getDeviceId() != 0) {
            device = deviceService.getById(dto.getDeviceId());
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
