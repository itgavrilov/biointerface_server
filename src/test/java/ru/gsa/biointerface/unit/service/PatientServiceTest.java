package ru.gsa.biointerface.unit.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.PatientRepository;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private PatientRepository repository;
    @Mock
    private IcdService icdService;

    @InjectMocks
    private PatientService service;

    @Test
    void findAll() {
        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        when(repository.findAllByIcd(null)).thenReturn(entities);

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
        List<Patient> entities = generator.objects(Patient.class, 5).toList();
        Icd icd = entities.get(0).getIcd();
        entities.forEach(e -> e.setIcd(icd));
        when(repository.findAllByIcd(icd.getId())).thenReturn(entities);

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

        verify(repository).findAllByIcd(icd.getId());
    }

    @Test
    void findAllPageable() {
        List<Patient> entities = generator.objects(Patient.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Patient> pageList = entities.subList(start, end);
            Page<Patient> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByIcd(null, pageable)).thenReturn(entityPage);

            Page<Patient> entityPageTests = service.findAll(null, pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            for (int i = 0; i < entityPage.getContent().size(); i++) {
                assertNotNull(entityPage.getContent().get(i));
                assertEquals(entityPage.getContent().get(i).getId(), entityPageTests.getContent().get(i).getId());
                assertEquals(entityPage.getContent().get(i).getFirstName(), entityPageTests.getContent().get(i).getFirstName());
                assertEquals(entityPage.getContent().get(i).getSecondName(), entityPageTests.getContent().get(i).getSecondName());
                assertEquals(entityPage.getContent().get(i).getPatronymic(), entityPageTests.getContent().get(i).getPatronymic());
                assertEquals(entityPage.getContent().get(i).getBirthday(), entityPageTests.getContent().get(i).getBirthday());
                assertEquals(entityPage.getContent().get(i).getIcd(), entityPageTests.getContent().get(i).getIcd());
                assertEquals(entityPage.getContent().get(i).getComment(), entityPageTests.getContent().get(i).getComment());
                assertIterableEquals(entityPage.getContent().get(i).getExaminations(), entityPageTests.getContent().get(i).getExaminations());
            }

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
        List<Patient> entities = generator.objects(Patient.class, 15).toList();
        Icd icd = entities.get(0).getIcd();
        entities.forEach(e -> e.setIcd(icd));
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Patient> pageList = entities.subList(start, end);
            Page<Patient> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByIcd(icd.getId(), pageable)).thenReturn(entityPage);

            Page<Patient> entityPageTests = service.findAll(icd.getId(), pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);

            for (int i = 0; i < entityPage.getContent().size(); i++) {
                assertNotNull(entityPage.getContent().get(i));
                assertEquals(entityPage.getContent().get(i).getId(), entityPageTests.getContent().get(i).getId());
                assertEquals(entityPage.getContent().get(i).getFirstName(), entityPageTests.getContent().get(i).getFirstName());
                assertEquals(entityPage.getContent().get(i).getSecondName(), entityPageTests.getContent().get(i).getSecondName());
                assertEquals(entityPage.getContent().get(i).getPatronymic(), entityPageTests.getContent().get(i).getPatronymic());
                assertEquals(entityPage.getContent().get(i).getBirthday(), entityPageTests.getContent().get(i).getBirthday());
                assertEquals(entityPage.getContent().get(i).getIcd(), icd);
                assertEquals(entityPage.getContent().get(i).getComment(), entityPageTests.getContent().get(i).getComment());
                assertIterableEquals(entityPage.getContent().get(i).getExaminations(), entityPageTests.getContent().get(i).getExaminations());
            }

            verify(repository).findAllByIcd(icd.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Patient entity = generator.nextObject(Patient.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

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
    void save() {
        PatientDTO dto = generator.nextObject(PatientDTO.class);
        Icd icd = generator.nextObject(Icd.class);
        dto.setIcdId(icd.getId());
        Patient entity = new Patient(
                dto.getId(),
                dto.getFirstName(),
                dto.getSecondName(),
                dto.getPatronymic(),
                dto.getBirthday(),
                icd,
                dto.getComment(),
                new ArrayList<>());

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(icdService.getById(icd.getId())).thenReturn(icd);

        Patient entityTest = service.saveOrUpdate(dto);
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

        verify(repository).findById(entity.getId());
        verify(icdService).getById(icd.getId());
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        Patient entity = generator.nextObject(Patient.class);
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