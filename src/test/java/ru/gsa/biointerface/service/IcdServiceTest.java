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
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class IcdServiceTest {

    @Autowired
    private IcdService service;
    @Autowired
    private IcdRepository repository;

    private final EasyRandom generator = new EasyRandom();

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
    }

    @Test
    void findAll() {
        List<Icd> entities = getNewEntityListFromDB(10, 5);

        List<Icd> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Icd entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_empty() {
        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAllPageable() {
        List<Icd> entities = getNewEntityListFromDB(10, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Icd> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Icd entity = entities.stream()
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
        Page<Icd> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Icd> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    void getById() {
        Icd entity = getNewEntityFromDB(10);

        Icd entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    void save() {
        Icd entity = getNewEntityWithoutIdAndTimestamps(10);

        Icd entityTest = service.save(entity.toBuilder().build());
        assertEqualsEntityWithoutIdAndTimestamps(entity, entityTest);

        Icd entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntity(entityFromBD, entityTest);

        assertNotEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update() {
        Icd entity = getNewEntityFromDB(10);

        Icd entityForTest = entity.toBuilder()
                .name(generator.nextObject(String.class))
                .version(11)
                .comment(generator.nextObject(String.class))
                .build();

        Icd entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertEquals(entityForTest.getCreationDate(), entityTest.getCreationDate());

        Icd entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityFromBD);
        assertEquals(entityForTest.getId(), entityFromBD.getId());
        assertEquals(entityForTest.getCreationDate(), entityFromBD.getCreationDate());

        assertEqualsEntity(entityFromBD, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getVersion(), entityTest.getVersion());
        assertNotEquals(entity.getComment(), entityTest.getComment());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update_rnd() {
        Icd entity = generator.nextObject(Icd.class);
        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
    }

    @Test
    void delete() {
        Icd entity = getNewEntityFromDB(10);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }

    private Icd getNewEntityWithoutIdAndTimestamps(int version){
        Icd entity = generator.nextObject(Icd.class);
        entity.setId(null);
        entity.setCreationDate(null);
        entity.setModifyDate(null);
        entity.setVersion(version);

        return entity;
    }

    private Icd getNewEntityFromDB(int version){
        Icd entity = repository.saveAndFlush(getNewEntityWithoutIdAndTimestamps(version));
        try {
            sleep(10);
        } catch (Exception ignored) {}

        return repository.getOrThrow(entity.getId());
    }

    private List<Icd> getNewEntityListFromDB(int version, int count){
        List<Icd> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntityWithoutIdAndTimestamps(version));
        }
        repository.saveAllAndFlush(entities);

        return repository.findAll();
    }

    private void assertEqualsEntity(Icd entity, Icd test) {
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertThat(entity.getCreationDate()).isEqualToIgnoringNanos(test.getCreationDate());
        assertThat(entity.getModifyDate()).isEqualToIgnoringNanos(test.getModifyDate());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(Icd entity, Icd test){
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getName(), test.getName());
        assertEquals(entity.getVersion(), test.getVersion());
        assertEquals(entity.getComment(), test.getComment());
    }
}