package ru.gsa.biointerface.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.exception.TransactionException;
import ru.gsa.biointerface.repository.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // для non-static BeforeAll и AfterAll
class ExaminationServiceTest {
    private static final String comment = "testComment";
    private static final Patient patient = new Patient(
            1,
            "secondNameTest",
            "firstNameTest",
            "middleNameTest",
            new GregorianCalendar(2021, Calendar.NOVEMBER, 27),
            null,
            comment);
    private static final Device device = new Device(1, 1);
    @Autowired
    private ExaminationService service;
    @Autowired
    private ExaminationRepository repository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DeviceRepository deviceRepository;


    @BeforeAll
    void setUp() {
        patientRepository.save(patient);
        deviceRepository.save(device);
    }

    @AfterAll
    void tearDown() {
        patientRepository.delete(patient);
        deviceRepository.delete(device);
    }

    @Test
    void findAll() {
        Examination entity = new Examination(patient, device, comment);
        entity = repository.save(entity);

        Set<Examination> examinations = service.findAll();
        Assertions.assertTrue(examinations.contains(entity));
        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void getByPatientRecord() {
        Examination entity = new Examination(patient, device, comment);
        Patient patientTest = entity.getPatient();
        Assertions.assertEquals(patient, patientTest);
    }

    @Test
    void findById() {
        Examination entity = new Examination(patient, device, comment);
        entity = repository.save(entity);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0));

        Examination entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
        Assertions.assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    void save() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setStarttime(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setDevice(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setPatient(null);
                    service.save(entityTest);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setChannels(null);
                    service.save(entityTest);
                });

        Examination entity = new Examination(patient, device, comment);
        entity = service.save(entity);
        Optional<Examination> optionalEntityTest = repository.findById(entity.getId());
        Assertions.assertTrue(optionalEntityTest.isPresent());
        Examination entityTest = optionalEntityTest.get();
        Assertions.assertEquals(entity, entityTest);
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patient, entityTest.getPatient());
        Assertions.assertEquals(comment, entityTest.getComment());
        entity = entityTest;

        String commentTest = comment + "Update";
        entity.setStarttime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setComment(commentTest);

        Examination finalEntity = entity;
        Assertions.assertDoesNotThrow(
                () -> service.save(finalEntity));


        optionalEntityTest = repository.findById(entity.getId());
        Assertions.assertTrue(optionalEntityTest.isPresent());
        Assertions.assertEquals(entity, optionalEntityTest.get());
        entityTest = optionalEntityTest.get();
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patient, entityTest.getPatient());
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
                        new Examination(patient, null, comment)
                ));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Examination entity =
                            new Examination(patient, device, comment);
                    entity.setChannels(null);
                    service.recordingStart(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entity =
                            new Examination(patient, device, comment);
                    entity.setChannels(new ArrayList<>());
                    service.recordingStart(entity);
                });
        Examination entity = new Examination(patient, device, comment);
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
        Examination entity = new Examination(patient, device, comment);
        entity = repository.save(entity);
        Channel channel = new Channel(0, entity, null);
        channel = channelRepository.save(channel);
        entity.getChannels().add(channel);
        Assertions.assertThrows(
                TransactionException.class,
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
        Examination entity = new Examination(patient, device, comment);
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
                () -> service.delete(0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setId(-1);
                    service.delete(entityTest.getId());
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setId(0);
                    service.delete(entityTest.getId());
                });
        int idTest = entity.getId();
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    Examination entityTest =
                            new Examination(patient, device, comment);
                    entityTest.setId(idTest + 1);
                    service.delete(entityTest.getId());
                });

        Optional<Examination> entityTest = repository.findById(entity.getId());
        Assertions.assertTrue(entityTest.isPresent());
        Assertions.assertEquals(entity, entityTest.get());
        Optional<Channel> channelTest = channelRepository.findById(channel.getId());
        Assertions.assertTrue(channelTest.isPresent());
        Assertions.assertEquals(channel, channelTest.get());
        service.delete(entityTest.get().getId());
        Assertions.assertFalse(repository.existsById(entityTest.get().getId()));
        Assertions.assertFalse(channelRepository.existsById(channelTest.get().getId()));
    }

    @Test
    void loadWithGraphsById() throws Exception {
        Examination entity = new Examination(patient, device, comment);
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
        Assertions.assertEquals(patient, entityTest.getPatient());
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