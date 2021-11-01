package ru.gsa.biointerface.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.configuration.ApplicationConfiguration;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.repository.PatientRecordRepository;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
class ExaminationServiceTest {
    private static final String comment = "testComment";
    private static final PatientRecord patientRecord = new PatientRecord(
            1,
            "secondNameTest",
            "firstNameTest",
            "middleNameTest",
            new GregorianCalendar(2021, Calendar.NOVEMBER, 27),
            null,
            comment);
    private static final Device device = new Device(1, 1);
    private static ExaminationService service;
    private static ExaminationRepository repository;
    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        service = context.getBean(ExaminationService.class);
        repository = context.getBean(ExaminationRepository.class);
        context.getBean(PatientRecordRepository.class).save(patientRecord);
        context.getBean(DeviceRepository.class).save(device);
    }

    @AfterAll
    static void tearDown() throws Exception {
        context.getBean(PatientRecordRepository.class).delete(patientRecord);
        context.getBean(DeviceRepository.class).delete(device);
        context.close();
    }

    @Test
    void getInstance() throws Exception {
        Assertions.assertSame(service, context.getBean(ExaminationService.class));
    }

    @Test
    void getAll() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        List<Examination> channelNames = service.getAll();
        Assertions.assertTrue(channelNames.contains(entity));
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void getByPatientRecord() {
        Examination entity =
                new Examination(patientRecord, device, comment);
        PatientRecord patientRecordTest = entity.getPatientRecord();
        Assertions.assertEquals(patientRecord, patientRecordTest);
    }

    @Test
    void getById() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1L));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0L));

        Examination entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void recordingStart() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        entity.recordingStart();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        entity.recordingStop();
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        Channel channel = new Channel(0, entity, null);
        entity.getChannels().add(channel);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        repository.delete(entity);
        Assertions.assertDoesNotThrow(
                () -> service.recordingStart(entity));
        Assertions.assertTrue(entity.isRecording());
        Assertions.assertTrue(repository.transactionIsOpen());
        entity.recordingStop();
        repository.transactionClose();
        Assertions.assertEquals(entity, repository.getById(entity.getId()));
        Channel channelTest = context.getBean(ChannelRepository.class).getById(new ChannelID(
                entity.getChannels().get(0).getId(),
                entity
        ));
        Assertions.assertEquals(channel, channelTest);
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void recordingStop() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        repository.insert(entity);
        entity.recordingStart();
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        Assertions.assertDoesNotThrow(
                () -> service.recordingStop(entity));

        Assertions.assertEquals(entity, repository.getById(entity.getId()));
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void delete() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        long id = entity.getId();
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.delete(null));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(-1);
                    service.delete(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(0);
                    service.delete(entity);
                });
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    entity.setId(id + 1);
                    service.delete(entity);
                });

        entity.setId(id);
        Assertions.assertEquals(entity, repository.getById(id));
        service.delete(entity);
        Assertions.assertNull(repository.getById(id));
    }

    @Test
    void update() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        long id = entity.getId();
        Date startTimeTest = Timestamp.valueOf(LocalDateTime.now());
        String commentTest = comment + "Update";
        entity.setStartTime(startTimeTest);
        entity.setComment(commentTest);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(null));
        entity.setId(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(id + 1);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(entity));
        entity.setId(id);
        entity.setStartTime(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setStartTime(startTimeTest);
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        Channel channel =  new Channel(0, entity, null);
        entity.getChannels().add(channel);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void loadWithGraphsById() throws Exception {
        Examination entity =
                new Examination(patientRecord, device, comment);
        Channel channel = new Channel(0, entity, null);
        entity.getChannels().add(channel);
        channel.getSamples().add(new Sample(0, channel, 10));

        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();

        Examination entityTest = service.loadWithGraphsById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patientRecord, entityTest.getPatientRecord());
        Assertions.assertEquals(comment, entityTest.getComment());
        Assertions.assertEquals(channel, entityTest.getChannels().get(0));
        Assertions.assertEquals(
                channel.getSamples().get(0),
                entityTest.getChannels().get(0).getSamples().get(0));

        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }
}