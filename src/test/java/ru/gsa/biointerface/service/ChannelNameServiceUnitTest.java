package ru.gsa.biointerface.service;

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
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelNameServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private ChannelNameRepository repository;

    @InjectMocks
    private ChannelNameService service;

    @Test
    void findAll() {
        List<ChannelName> entities = getNewEntity(5);
        when(repository.findAll()).thenReturn(entities);

        List<ChannelName> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            ChannelName entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<ChannelName> entities = getNewEntity(15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<ChannelName> pageList = entities.subList(start, end);
            Page<ChannelName> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<ChannelName> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                ChannelName entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            verify(repository).findAll(pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ChannelName> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        Page<ChannelName> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertIterableEquals(entityPage, entityPageTests);
        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        ChannelName entity = getNewEntity();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        ChannelName entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    @Test
    void save() {
        ChannelName entity = getNewEntity();
        ChannelName entityClone = entity.toBuilder().build();
        when(repository.existsByName(entityClone.getName())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        ChannelName entityTest = service.save(entityClone);
        assertEqualsEntity(entity, entityTest);

        verify(repository).existsByName(entityClone.getName());
        verify(repository).save(entityClone);
    }

    @Test
    void update() {
        ChannelName entity = getNewEntity();

        ChannelName entityForTest = entity.toBuilder()
                .name(generator.nextObject(String.class))
                .comment(generator.nextObject(String.class))
                .build();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity.toBuilder().build());

        ChannelName entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());

        assertEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update_rnd() {
        ChannelName entity = getNewEntity();
        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        when(repository.getOrThrow(entity.getId())).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void delete() {
        ChannelName entity = getNewEntity();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
        verify(repository).getOrThrow(entity.getId());
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    private ChannelName getNewEntity() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    private List<ChannelName> getNewEntity(int count) {
        List<ChannelName> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntity());
        }

        return entities;
    }

    private void assertEqualsEntity(ChannelName entity, ChannelName test) {
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(ChannelName entity, ChannelName test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getName(), test.getName());
        assertEquals(entity.getComment(), test.getComment());
    }
}