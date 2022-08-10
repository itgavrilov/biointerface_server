package ru.gsa.biointerface.service;

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
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;

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
class ChannelNameServiceTest {

    @Autowired
    private ChannelNameService service;
    @Autowired
    private ChannelNameRepository repository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @Transactional
    void findAll() {
        List<ChannelName> entities = generator.objects(ChannelName.class, 5).toList();
        entities.forEach(entity -> entity.setId(null));
        entities = repository.saveAll(entities);

        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertNotNull(entities.get(i));
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getName(), entityTests.get(i).getName());
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertIterableEquals(entities.get(i).getChannels(), entityTests.get(i).getChannels());
        }
    }

    @Test
    void findAll_empty() {
        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    @Transactional
    void findAllPageable() {
        List<ChannelName> entities = generator.objects(ChannelName.class, 15).toList();
        entities.forEach(entity -> entity.setId(null));
        entities = repository.saveAll(entities);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<ChannelName> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            List<ChannelName> finalEntities = entities;
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                ChannelName entity = finalEntities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEquals(entity.getId(), entityTest.getId());
                assertEquals(entity.getName(), entityTest.getName());
                assertEquals(entity.getComment(), entityTest.getComment());
                assertIterableEquals(entity.getChannels(), entityTest.getChannels());
            });
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ChannelName> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<ChannelName> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertIterableEquals(entityPage, entityPageTests);
    }

    @Test
    @Transactional
    void getById() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        entity.setId(null);
        entity = repository.save(entity);

        ChannelName entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getChannels(), entityTest.getChannels());
    }

    @Test
    void getById_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
    }

    @Test
    @Transactional
    void save() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        entity.setId(null);
        entity.setChannels(new ArrayList<>());

        ChannelName entityNew = new ChannelName(
                null,
                entity.getName(),
                entity.getComment(),
                new ArrayList<>());

        ChannelName entityTest = service.save(entityNew);
        assertNotNull(entityTest);
        assertNotNull(entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getChannels(), entityTest.getChannels());
    }

    @Test
    @Transactional
    void update() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        entity.setId(null);
        entity.setChannels(new ArrayList<>());
        entity = repository.save(entity);

        ChannelName entityNew = new ChannelName(
                entity.getId(),
                generator.nextObject(String.class),
                generator.nextObject(String.class),
                new ArrayList<>());

        ChannelName entityTest = service.update(entityNew);
        assertNotNull(entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entityNew.getName(), entityTest.getName());
        assertEquals(entityNew.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getChannels(), entityTest.getChannels());
    }

    @Test
    void update_rnd() {
        ChannelName entity = generator.nextObject(ChannelName.class);

        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        assertThrows(NotFoundException.class, () -> service.update(entity), message);
    }

    @Test
    @Transactional
    void delete() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        entity.setId(null);
        entity = repository.save(entity);

        ChannelName finalEntity = entity;
        assertDoesNotThrow(() -> service.delete(finalEntity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);

        assertThrows(NotFoundException.class, () -> service.delete(rnd), message);
    }
}