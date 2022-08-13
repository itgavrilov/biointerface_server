package ru.gsa.biointerface.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.repository.PatientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class PatientServiceTest {

    @Autowired
    private PatientService service;
    @Autowired
    private PatientRepository repository;
    @Autowired
    private IcdRepository icdRepository;

    private final EasyRandom generator = new EasyRandom();

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
        icdRepository.deleteAll(icdRepository.findAll());
    }

    @Test
    void findAll() {
        List<Patient> entities = getNewEntityListFromDB(null, 5);

        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Patient entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_empty() {
        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAll_byIcd() {
        Icd icd = getIcdFromDB(10);
        List<Patient> entities = getNewEntityListFromDB(icd, 5);

        List<Patient> entityTests = service.findAll(icd.getId());
        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Patient entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAllPageable() {
        List<Patient> entities = getNewEntityListFromDB(null, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Patient> entityPageTests = service.findAll(null, pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Patient> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Patient> entityPageTests = service.findAll(null, pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    void findAllPageable_byIcd() {
        Icd icd = getIcdFromDB(10);
        List<Patient> entities = getNewEntityListFromDB(icd, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Patient> entityPageTests = service.findAll(icd.getId(), pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Patient entity = getNewEntityFromDB(null, 10);

        Patient entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    void saveWithoutIcd() {
        Patient entity = getNewEntityWithoutIdAndTimestamps(null, 10);

        Patient entityTest = service.save(entity.toBuilder().build());
        assertEqualsEntityWithoutIdAndTimestamps(entity, entityTest);

        Patient entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntity(entityFromBD, entityTest);

        assertNotEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void save() {
        Icd icd = getIcdFromDB(10);
        Patient entity = getNewEntityWithoutIdAndTimestamps(icd, 10);

        Patient entityTest = service.save(entity.toBuilder().build());
        assertEqualsEntityWithoutIdAndTimestamps(entity, entityTest);

        Patient entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntity(entityFromBD, entityTest);

        assertNotEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update() {
        Patient entity = getNewEntityFromDB(null, 10);

        Icd icd = getIcdFromDB(10);
        Patient entityForTest = getNewEntityWithoutIdAndTimestamps(icd, 50);
        entityForTest.setId(entity.getId());
        entityForTest.setCreationDate(entity.getCreationDate());
        entityForTest.setModifyDate(entity.getModifyDate());

        Patient entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertEquals(entityForTest.getCreationDate(), entityTest.getCreationDate());

        Patient entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityFromBD);
        assertEquals(entityForTest.getId(), entityFromBD.getId());
        assertEquals(entityForTest.getCreationDate(), entityFromBD.getCreationDate());

        assertEqualsEntity(entityFromBD, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getFirstName(), entityTest.getFirstName());
        assertNotEquals(entity.getSecondName(), entityTest.getSecondName());
        assertNotEquals(entity.getPatronymic(), entityTest.getPatronymic());
        assertNotEquals(entity.getBirthday(), entityTest.getBirthday());
        assertNotEquals(entity.getIcd(), entityTest.getIcd());
        assertNotEquals(entity.getComment(), entityTest.getComment());
    }

    @Test
    void delete() {
        Icd icd = getIcdFromDB(10);
        Patient entity = getNewEntityFromDB(icd, 10);

        assertDoesNotThrow(() -> service.delete(entity.getId()));

        icdRepository.findById(icd.getId()).orElseThrow();
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }

    private Icd getIcdFromDB(int version){
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(null);
        icd.setVersion(10);
        return icdRepository.save(icd);
    }

    private Patient getNewEntityWithoutIdAndTimestamps(Icd icd, int minusDays){
        Patient entity = generator.nextObject(Patient.class);
        entity.setBirthday(LocalDate.now().minusDays(minusDays));
        entity.setId(null);
        entity.setCreationDate(null);
        entity.setModifyDate(null);
        entity.setIcd(icd);

        return entity;
    }

    private Patient getNewEntityFromDB(Icd icd, int minusDays){
        Patient entity = repository.saveAndFlush(getNewEntityWithoutIdAndTimestamps(icd, minusDays));
        try {
            sleep(10);
        } catch (Exception ignored) {}

        return repository.getOrThrow(entity.getId());
    }

    private List<Patient> getNewEntityListFromDB(Icd icd, int count){
        List<Patient> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntityWithoutIdAndTimestamps(icd, 10));
        }
        repository.saveAll(entities);

        return repository.findAll();
    }

    private void assertEqualsEntity(Patient entity, Patient test){
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getCreationDate(), test.getCreationDate());
        assertEquals(entity.getModifyDate(), test.getModifyDate());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(Patient entity, Patient test){
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getFirstName(), test.getFirstName());
        assertEquals(entity.getSecondName(), test.getSecondName());
        assertEquals(entity.getPatronymic(), test.getPatronymic());
        assertEquals(entity.getBirthday(), test.getBirthday());
        assertEquals(entity.getIcd(), test.getIcd());
        assertEquals(entity.getComment(), test.getComment());
    }
}