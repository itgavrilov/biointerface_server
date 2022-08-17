package ru.gsa.biointerface.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;
import ru.gsa.biointerface.service.PatientService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.gsa.biointerface.utils.IcdUtil.getIcd;
import static ru.gsa.biointerface.utils.PatientUtil.assertEqualsPatient;
import static ru.gsa.biointerface.utils.PatientUtil.assertEqualsPatientLight;
import static ru.gsa.biointerface.utils.PatientUtil.getPatient;
import static ru.gsa.biointerface.utils.PatientUtil.getPatients;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    @Test
    void findAll() {
        List<Patient> entities = getPatients(null, 5);
        when(repository.findAllByIcd(null)).thenReturn(entities);

        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Patient entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsPatient(entity, entityTest);
        });

        verify(repository).findAllByIcd(null);
    }

    @Test
    void findAll_empty() {
        when(repository.findAllByIcd(null)).thenReturn(new ArrayList<>());

        List<Patient> entityTests = service.findAll(null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAllByIcd(null);
    }

    @Test
    void findAll_byIcd() {
        Icd icd = getIcd(10);
        List<Patient> entities = getPatients(icd, 5);
        when(repository.findAllByIcd(icd.getId())).thenReturn(entities);

        List<Patient> entityTests = service.findAll(icd.getId());
        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Patient entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsPatient(entity, entityTest);
        });

        verify(repository).findAllByIcd(icd.getId());
    }

    @Test
    void findAllPageable() {
        List<Patient> entities = getPatients(null, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Patient> pageList = entities.subList(start, end);
            Page<Patient> entityPage = new PageImpl<>(pageList, pageable, entities.size());
            when(repository.findAllByIcd(null, pageable)).thenReturn(entityPage);

            Page<Patient> entityPageTests = service.findAll(null, pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsPatient(entity, entityTest);
            });

            verify(repository).findAllByIcd(null, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Patient> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAllByIcd(null, pageable)).thenReturn(entityPage);

        Page<Patient> entityPageTests = service.findAll(null, pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
        verify(repository).findAllByIcd(null, pageable);
    }

    @Test
    void findAllPageable_byIcd() {
        Icd icd = getIcd(10);
        List<Patient> entities = getPatients(icd, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Patient> pageList = entities.subList(start, end);
            Page<Patient> entityPage = new PageImpl<>(pageList, pageable, entities.size());
            when(repository.findAllByIcd(icd.getId(), pageable)).thenReturn(entityPage);

            Page<Patient> entityPageTests = service.findAll(icd.getId(), pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Patient entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsPatient(entity, entityTest);
            });

            verify(repository).findAllByIcd(icd.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Patient entity = getPatient(null, 10);

        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Patient entityTest = service.getById(entity.getId());

        assertEqualsPatient(entity, entityTest);

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        when(repository.getOrThrow(rndId)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
        verify(repository).getOrThrow(rndId);
    }

    @Test
    void saveWithoutIcd() {
        Patient entity = getPatient(null, 10);
        Patient entityClone = entity.toBuilder().build();

        when(repository.existsBySecondNameAndFirstNameAndPatronymicAndBirthday(entityClone.getSecondName(),
                entityClone.getFirstName(), entityClone.getPatronymic(), entityClone.getBirthday())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        Patient entityTest = service.save(entityClone);
        assertEqualsPatient(entity, entityTest);

        verify(repository).existsBySecondNameAndFirstNameAndPatronymicAndBirthday(entityClone.getSecondName(),
                entityClone.getFirstName(), entityClone.getPatronymic(), entityClone.getBirthday());
        verify(repository).save(entityClone);
    }

    @Test
    void save() {
        Icd icd = getIcd(10);
        Patient entity = getPatient(icd, 10);
        Patient entityClone = entity.toBuilder().build();

        when(repository.existsBySecondNameAndFirstNameAndPatronymicAndBirthday(entityClone.getSecondName(),
                entityClone.getFirstName(), entityClone.getPatronymic(), entityClone.getBirthday())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        Patient entityTest = service.save(entityClone);
        assertEqualsPatient(entity, entityTest);

        verify(repository).existsBySecondNameAndFirstNameAndPatronymicAndBirthday(entityClone.getSecondName(),
                entityClone.getFirstName(), entityClone.getPatronymic(), entityClone.getBirthday());
        verify(repository).save(entityClone);
    }

    @Test
    @Transactional
    void update() {
        Patient entity = getPatient(null, 10);
        Patient entityClone = entity.toBuilder().build();

        Icd icd = getIcd(10);
        Patient entityForTest = getPatient(icd, 50);
        entityForTest.setId(entity.getId());
        entityForTest.setCreationDate(entity.getCreationDate());
        entityForTest.setModifyDate(entity.getModifyDate());

        when(repository.getOrThrow(entity.getId())).thenReturn(entityClone);

        Patient entityTest = service.update(entityForTest);
        assertEqualsPatientLight(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());

        assertEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getFirstName(), entityTest.getFirstName());
        assertNotEquals(entity.getSecondName(), entityTest.getSecondName());
        assertNotEquals(entity.getPatronymic(), entityTest.getPatronymic());
        assertNotEquals(entity.getBirthday(), entityTest.getBirthday());
        assertNotEquals(entity.getIcd(), entityTest.getIcd());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void delete() {
        Patient entity = getPatient(null, 10);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
        verify(repository).getOrThrow(entity.getId());
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        when(repository.getOrThrow(rndId)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
        verify(repository).getOrThrow(rndId);
    }
}