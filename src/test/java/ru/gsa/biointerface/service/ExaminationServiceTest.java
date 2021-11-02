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
    static void setUp() {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        service = context.getBean(ExaminationService.class);
        repository = context.getBean(ExaminationRepository.class);
        context.getBean(PatientRecordRepository.class).save(patientRecord);
        context.getBean(DeviceRepository.class).save(device);
    }

    @AfterAll
    static void tearDown() {
        context.getBean(PatientRecordRepository.class).delete(patientRecord);
        context.getBean(DeviceRepository.class).delete(device);
        context.close();
    }

    @Test
    void getService() {
        Assertions.assertSame(service, context.getBean(ExaminationService.class));
    }

    @Test
    void getAll() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
        repository.transactionClose();

        Assertions.assertTrue(repository.existsById(entity.getId()));
        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void getByPatientRecord() {
        Examination entity = new Examination(patientRecord, device, comment);
        PatientRecord patientRecordTest = entity.getPatientRecord();
        Assertions.assertEquals(patientRecord, patientRecordTest);
    }

    @Test
    void findById() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
        repository.transactionClose();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.findById(-1L));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.findById(0L));

        Examination entityTest = service.findById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void recordingStart() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        entity.recordingStart();
        Examination finalEntity = entity;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(finalEntity));
        entity.recordingStop();
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(finalEntity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(finalEntity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(finalEntity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(finalEntity));
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
        repository.transactionClose();
        Examination finalEntity1 = entity;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(finalEntity1));
        repository.delete(entity);

        entity = service.recordingStart(entity);
        Channel channel = entity.getChannels().get(0);

        Assertions.assertTrue(entity.isRecording());
        Assertions.assertTrue(repository.transactionIsOpen());
        entity.recordingStop();
        repository.transactionClose();
        Assertions.assertEquals(entity, repository.findById(entity.getId()).get());
        Optional<Channel> channelTest = context.getBean(ChannelRepository.class).findById(new ChannelID(
                entity.getChannels().get(0).getId(),
                entity
        ));
        Assertions.assertTrue(channelTest.isPresent());
        Assertions.assertEquals(channel, channelTest.get());
        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void recordingStop() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
        entity.recordingStart();

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        Examination finalEntity = entity;
        Assertions.assertDoesNotThrow(
                () -> service.recordingStop(finalEntity));

        Assertions.assertEquals(entity, repository.findById(entity.getId()).get());

        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void delete() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
        repository.transactionClose();

        long id = entity.getId();

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.delete(null));

        Examination finalEntity = entity;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    finalEntity.setId(-1);
                    service.delete(finalEntity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    finalEntity.setId(0);
                    service.delete(finalEntity);
                });
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    finalEntity.setId(id + 1);
                    service.delete(finalEntity);
                });
        entity.setId(id);
        Optional<Examination> optional = repository.findById(id);

        Examination finalEntity1 = entity;
        Assertions.assertTrue(
            optional.map(e -> e.equals(finalEntity1)).orElse(false)
        );
        Assertions.assertDoesNotThrow(() -> service.delete(finalEntity1));
        Assertions.assertFalse(repository.existsById(finalEntity1.getId()));
    }

    @Test
    void update() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));
        repository.transactionOpen();
        entity = repository.insert(entity);
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
        Examination finalEntity = entity;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(finalEntity));
        entity.setId(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(finalEntity));
        entity.setId(id + 1);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(finalEntity));
        entity.setId(id);
        entity.setStartTime(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(finalEntity));
        entity.setStartTime(startTimeTest);
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(finalEntity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(finalEntity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(finalEntity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(finalEntity));
        Channel channel =  new Channel(0, entity, null);
        entity.getChannels().add(channel);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(finalEntity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(finalEntity));

        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void loadWithGraphsById() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(new Channel(0, entity, null));

        repository.transactionOpen();
        entity = repository.insert(entity);
        Channel channel = entity.getChannels().get(0);
        channel.getSamples().add(new Sample(0, channel, 10));
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

        System.out.println("11111111111");
        repository.delete(entity);
        System.out.println("22222222222");
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }
}