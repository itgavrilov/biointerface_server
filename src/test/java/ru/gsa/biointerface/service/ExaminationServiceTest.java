package ru.gsa.biointerface.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.configuration.ApplicationConfiguration;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.*;
import ru.gsa.biointerface.repository.exception.TransactionNotOpenException;

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
    private static ChannelRepository channelRepository;
    private static SampleRepository sampleRepository;
    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void setUp() {
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        service = context.getBean(ExaminationService.class);
        repository = context.getBean(ExaminationRepository.class);
        channelRepository = context.getBean(ChannelRepository.class);
        sampleRepository = context.getBean(SampleRepository.class);
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
    void findAll() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity = repository.save(entity);

        List<Examination>  examinations = service.findAll();
        Assertions.assertTrue(examinations.contains(entity));
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
        entity = repository.save(entity);

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
    void save() throws Exception {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setStartTime(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setDevice(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setPatientRecord(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setChannels(null);
                    service.save(entityTest);
                });

        Examination entity = new Examination(patientRecord, device, comment);
        entity = service.save(entity);
        Optional<Examination> optionalEntityTest = repository.findById(entity.getId());
        Assertions.assertTrue(optionalEntityTest.isPresent());
        Examination entityTest = optionalEntityTest.get();
        Assertions.assertEquals(entity, entityTest);
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patientRecord, entityTest.getPatientRecord());
        Assertions.assertEquals(comment, entityTest.getComment());
        entity = entityTest;

        String commentTest = comment + "Update";
        entity.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        Date startTimeTest = entity.getStartTime();
        entity.setComment(commentTest);

        Examination finalEntity = entity;
        Assertions.assertDoesNotThrow(
                () -> service.save(finalEntity));


        optionalEntityTest = repository.findById(entity.getId());
        Assertions.assertTrue(optionalEntityTest.isPresent());
        Assertions.assertEquals(entity, optionalEntityTest.get());
        entityTest = optionalEntityTest.get();
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patientRecord, entityTest.getPatientRecord());
        Assertions.assertEquals(commentTest, entityTest.getComment());

        repository.delete(entityTest);
        Assertions.assertFalse(repository.existsById(entityTest.getId()));
    }

    @Test
    void recordingStart() throws Exception {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(
                        new Examination(null, device, comment)
                ));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(
                        new Examination(patientRecord, null, comment)
                ));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entity =
                            new Examination(patientRecord, device, comment);
                    entity.setChannels(null);
                    service.recordingStart(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entity =
                        new Examination(patientRecord, device, comment);
                    entity.setChannels(new ArrayList<>());
                    service.recordingStart(entity);
                });
        Examination entity = new Examination(patientRecord, device, comment);
        entity.getChannels().add(null);
        Examination finalEntity = entity;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(finalEntity));
        entity.setChannels(new ArrayList<>());
        entity = repository.save(entity);
        Channel channel = new Channel(0, entity, null);
        channel = channelRepository.save(channel);
        entity.getChannels().add(channel);
        service.recordingStart(entity);
        Assertions.assertTrue(service.isRecording());
        Assertions.assertTrue(sampleRepository.transactionIsOpen());
        Sample sample = new Sample(0, channel, 10);
        sampleRepository.insert(sample);
        sampleRepository.transactionClose();
        Optional<Examination> entityTest = repository.findById(entity.getId());
        Assertions.assertTrue(entityTest.isPresent());
        Assertions.assertEquals(entity, entityTest.get());
        Optional<Channel> channelTest = channelRepository.findById(channel.getId());
        Assertions.assertTrue(channelTest.isPresent());
        Assertions.assertEquals(channel, channelTest.get());
        Optional<Sample> sampleTest = sampleRepository.findById(sample.getId());
        Assertions.assertTrue(sampleTest.isPresent());
        Assertions.assertEquals(sample, sampleTest.get());
        repository.delete(entityTest.get());
        Assertions.assertFalse(repository.existsById(entityTest.get().getId()));
        Assertions.assertFalse(channelRepository.existsById(channelTest.get().getId()));
        Assertions.assertFalse(sampleRepository.existsById(sampleTest.get().getId()));
    }

    @Test
    void recordingStop() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity = repository.save(entity);
        Channel channel = new Channel(0, entity, null);
        channel = channelRepository.save(channel);
        entity.getChannels().add(channel);
        Assertions.assertThrows(
                TransactionNotOpenException.class,
                () -> service.recordingStop());
        sampleRepository.transactionOpen();
        Assertions.assertDoesNotThrow(
                () -> service.recordingStop());
        Optional<Examination> entityTest = repository.findById(entity.getId());
        Assertions.assertTrue(entityTest.isPresent());
        Assertions.assertEquals(entity, entityTest.get());
        Optional<Channel> channelTest = channelRepository.findById(channel.getId());
        Assertions.assertTrue(channelTest.isPresent());
        Assertions.assertEquals(channel, channelTest.get());
        repository.delete(entityTest.get());
        Assertions.assertFalse(repository.existsById(entityTest.get().getId()));
        Assertions.assertFalse(channelRepository.existsById(channelTest.get().getId()));
    }

    @Test
    void delete() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity = repository.save(entity);
        Channel channel = new Channel(0, entity, null);
        channel = channelRepository.save(channel);
        entity.getChannels().add(channel);
        sampleRepository.transactionOpen();
        Sample sample = new Sample(0, channel, 10);
        sampleRepository.insert(sample);
        sampleRepository.transactionClose();

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.delete(null));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setId(-1L);
                    service.delete(entityTest);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setId(0L);
                    service.delete(entityTest);
                });
        Long idTest = entity.getId();
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patientRecord, device, comment);
                    entityTest.setId(idTest+1);
                    service.delete(entityTest);
                });

        Optional<Examination> entityTest = repository.findById(entity.getId());
        Assertions.assertTrue(entityTest.isPresent());
        Assertions.assertEquals(entity, entityTest.get());
        Optional<Channel> channelTest = channelRepository.findById(channel.getId());
        Assertions.assertTrue(channelTest.isPresent());
        Assertions.assertEquals(channel, channelTest.get());
        service.delete(entityTest.get());
        Assertions.assertFalse(repository.existsById(entityTest.get().getId()));
        Assertions.assertFalse(channelRepository.existsById(channelTest.get().getId()));
    }

    @Test
    void loadWithGraphsById() throws Exception {
        Examination entity = new Examination(patientRecord, device, comment);
        entity = repository.save(entity);
        Channel channel = new Channel(0, entity, null);
        channel = channelRepository.save(channel);
        entity.getChannels().add(channel);
        sampleRepository.transactionOpen();
        Sample sample = new Sample(0, channel, 10);
        channel.getSamples().add(sample);
        sampleRepository.insert(sample);
        sampleRepository.transactionClose();

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
        Assertions.assertFalse(repository.existsById(entity.getId()));
        Assertions.assertFalse(channelRepository.existsById(channel.getId()));
        Assertions.assertFalse(sampleRepository.existsById(sample.getId()));
    }
}