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
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.service.IcdService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class IcdServiceTest {

    @Autowired
    private IcdService service;
    @Autowired
    private IcdRepository repository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @Transactional
    void findAll() {
        List<Icd> entities = generator.objects(Icd.class, 5).toList();
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setVersion(generator.nextInt(10, 99));
            entity.setPatients(new ArrayList<>());
        });
        entities = repository.saveAll(entities);

        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getName(), entityTests.get(i).getName());
            assertEquals(entities.get(i).getVersion(), entityTests.get(i).getVersion());
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertIterableEquals(entities.get(i).getPatients(), entityTests.get(i).getPatients());
        }
    }

    @Test
    void findAll_empty() {
        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    @Transactional
    void findAllPageable() {
        List<Icd> entities = generator.objects(Icd.class, 15).toList();
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setVersion(generator.nextInt(10, 99));
            entity.setPatients(new ArrayList<>());
        });
        entities = repository.saveAll(entities);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Icd> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            List<Icd> finalEntities = entities;
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Icd entity = finalEntities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();

                assertEquals(entity.getId(), entityTest.getId());
                assertEquals(entity.getName(), entityTest.getName());
                assertEquals(entity.getVersion(), entityTest.getVersion());
                assertEquals(entity.getComment(), entityTest.getComment());
                assertIterableEquals(entity.getPatients(), entityTest.getPatients());
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Icd> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Icd> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    @Transactional
    void getById() {
        Icd entity = generator.nextObject(Icd.class);
        entity.setId(null);
        entity.setVersion(generator.nextInt(10, 99));
        entity.setPatients(new ArrayList<>());
        entity = repository.save(entity);

        Icd entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getVersion(), entityTest.getVersion());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getPatients(), entityTest.getPatients());
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    @Transactional
    void saveOrUpdate() {
        IcdDTO dto = generator.nextObject(IcdDTO.class);
        dto.setId(null);
        dto.setVersion(generator.nextInt(10, 99));

        Icd entityTest = service.saveOrUpdate(dto);
        assertNotNull(entityTest);
        Icd entity = repository.findById(entityTest.getId()).orElseThrow();
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getVersion(), entityTest.getVersion());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getPatients(), entityTest.getPatients());

        assertEquals(dto.getName(), entityTest.getName());
        assertEquals(dto.getVersion(), entityTest.getVersion());
        assertEquals(dto.getComment(), entityTest.getComment());
    }

    @Test
    void delete() {
        Icd entity = generator.nextObject(Icd.class);
        entity.setId(null);
        entity.setVersion(generator.nextInt(10, 99));
        entity.setPatients(new ArrayList<>());
        entity = repository.save(entity);

        Icd finalEntity = entity;
        assertDoesNotThrow(() -> service.delete(finalEntity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }
}