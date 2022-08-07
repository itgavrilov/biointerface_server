package ru.gsa.biointerface.integration.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.repository.PatientRepository;
import ru.gsa.biointerface.service.PatientService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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

    @Test
    @Transactional
    void findAll() {
        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        entities.forEach(e -> {
            e.setId(null);
            e.setBirthday(LocalDateTime.now().minusMinutes(1));
            e.setIcd(null);
            e.setExaminations(new ArrayList<>());
        });
        entities = repository.saveAll(entities);

        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertNotNull(entities.get(i));
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getFirstName(), entityTests.get(i).getFirstName());
            assertEquals(entities.get(i).getSecondName(), entityTests.get(i).getSecondName());
            assertEquals(entities.get(i).getPatronymic(), entityTests.get(i).getPatronymic());
            assertEquals(entities.get(i).getBirthday(), entityTests.get(i).getBirthday());
            assertEquals(entities.get(i).getIcd(), entityTests.get(i).getIcd());
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertIterableEquals(entities.get(i).getExaminations(), entityTests.get(i).getExaminations());
        }
    }

    @Test
    void findAll_empty() {
        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    @Transactional
    void findAll_byIcd() {
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(null);
        icd.setVersion(10);
        icd.setPatients(new ArrayList<>());
        icd = icdRepository.save(icd);

        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        Icd finalIcd = icd;
        entities.forEach(e -> {
            e.setId(null);
            e.setBirthday(LocalDateTime.now().minusMinutes(1));
            e.setIcd(finalIcd);
            e.setExaminations(new ArrayList<>());
        });
        repository.saveAll(entities);

        List<Patient> entityTests = service.findAll(icd.getId());
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertNotNull(entities.get(i));
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getFirstName(), entityTests.get(i).getFirstName());
            assertEquals(entities.get(i).getSecondName(), entityTests.get(i).getSecondName());
            assertEquals(entities.get(i).getPatronymic(), entityTests.get(i).getPatronymic());
            assertEquals(entities.get(i).getBirthday(), entityTests.get(i).getBirthday());
            assertEquals(entities.get(i).getIcd(), icd);
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertIterableEquals(entities.get(i).getExaminations(), entityTests.get(i).getExaminations());
        }
    }

    @Test
    @Transactional
    void findAllPageable() {
        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        entities.forEach(e -> {
            e.setId(null);
            e.setBirthday(LocalDateTime.now().minusMinutes(1));
            e.setIcd(null);
            e.setExaminations(new ArrayList<>());
        });
        entities = repository.saveAll(entities);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Patient> entityPageTests = service.findAll(null, pageable);
            assertNotNull(entityPageTests);

            List<Patient> finalEntities = entities;
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = finalEntities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();

                assertEquals(entity.getId(), entityTest.getId());
                assertEquals(entity.getFirstName(), entityTest.getFirstName());
                assertEquals(entity.getSecondName(), entityTest.getSecondName());
                assertEquals(entity.getPatronymic(), entityTest.getPatronymic());
                assertEquals(entity.getBirthday(), entityTest.getBirthday());
                assertEquals(entity.getIcd(), entityTest.getIcd());
                assertEquals(entity.getComment(), entityTest.getComment());
                assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
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
    @Transactional
    void findAllPageable_byIcd() {
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(null);
        icd.setVersion(10);
        icd.setPatients(new ArrayList<>());
        icd = icdRepository.save(icd);

        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        Icd finalIcd = icd;
        entities.forEach(e -> {
            e.setId(null);
            e.setBirthday(LocalDateTime.now().minusMinutes(1));
            e.setIcd(finalIcd);
            e.setExaminations(new ArrayList<>());
        });
        entities = repository.saveAll(entities);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Patient> entityPageTests = service.findAll(icd.getId(), pageable);
            assertNotNull(entityPageTests);

            List<Patient> finalEntities = entities;
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = finalEntities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();

                assertEquals(entity.getId(), entityTest.getId());
                assertEquals(entity.getFirstName(), entityTest.getFirstName());
                assertEquals(entity.getSecondName(), entityTest.getSecondName());
                assertEquals(entity.getPatronymic(), entityTest.getPatronymic());
                assertEquals(entity.getBirthday(), entityTest.getBirthday());
                assertEquals(entity.getIcd(), entityTest.getIcd());
                assertEquals(entity.getComment(), entityTest.getComment());
                assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    @Transactional
    void getById() {
        Patient entity = generator.nextObject(Patient.class);
        entity.setId(null);
        entity.setBirthday(LocalDateTime.now().minusMinutes(1));
        entity.setIcd(null);
        entity.setExaminations(new ArrayList<>());
        entity = repository.save(entity);

        Patient entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getFirstName(), entityTest.getFirstName());
        assertEquals(entity.getSecondName(), entityTest.getSecondName());
        assertEquals(entity.getPatronymic(), entityTest.getPatronymic());
        assertEquals(entity.getBirthday(), entityTest.getBirthday());
        assertEquals(entity.getIcd(), entityTest.getIcd());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    @Transactional
    void save() {
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(null);
        icd.setVersion(10);
        icd.setPatients(new ArrayList<>());
        icd = icdRepository.save(icd);

        PatientDTO dto = generator.nextObject(PatientDTO.class);
        dto.setId(null);
        dto.setBirthday(LocalDateTime.now().minusMinutes(1));
        dto.setIcdId(icd.getId());

        Patient entityTest = service.saveOrUpdate(dto);
        assertNotNull(entityTest);
        Patient entity = repository.findById(entityTest.getId()).orElseThrow();
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getFirstName(), entityTest.getFirstName());
        assertEquals(entity.getSecondName(), entityTest.getSecondName());
        assertEquals(entity.getPatronymic(), entityTest.getPatronymic());
        assertEquals(entity.getBirthday(), entityTest.getBirthday());
        assertEquals(entity.getIcd(), entityTest.getIcd());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());

        assertEquals(dto.getFirstName(), entityTest.getFirstName());
        assertEquals(dto.getSecondName(), entityTest.getSecondName());
        assertEquals(dto.getPatronymic(), entityTest.getPatronymic());
        assertEquals(dto.getBirthday(), entityTest.getBirthday());
        assertEquals(dto.getIcdId(), entityTest.getIcd().getId());
        assertEquals(dto.getComment(), entityTest.getComment());
    }

    @Test
    @Transactional
    void delete() {
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(null);
        icd.setVersion(10);
        icd.setPatients(new ArrayList<>());
        icd = icdRepository.save(icd);

        Patient entity = generator.nextObject(Patient.class);
        entity.setId(null);
        entity.setBirthday(LocalDateTime.now().minusMinutes(1));
        entity.setIcd(icd);
        entity.setExaminations(new ArrayList<>());
        repository.save(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));

        icdRepository.findById(icd.getId()).orElseThrow();
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }
}