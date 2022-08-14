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
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IcdServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private IcdRepository repository;

    @InjectMocks
    private IcdService service;

    @Test
    void findAll() {
        List<Icd> entities = getNewEntityList(5);
        when(repository.findAll()).thenReturn(entities);

        List<Icd> entityTests = service.findAll();

        entityTests.forEach(entityTest -> {
            Icd entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);

        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<Icd> entities = getNewEntityList(15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Icd> pageList = entities.subList(start, end);
            Page<Icd> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<Icd> entityPageTests = service.findAll(pageable);
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Icd entity = entities.stream()
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
        Page<Icd> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        Page<Icd> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);

        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        Icd entity = getNewEntity();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Icd entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);

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
        Icd entity = getNewEntity();
        Icd entityClone = entity.toBuilder().build();
        when(repository.existsByNameAndVersion(entityClone.getName(), entityClone.getVersion())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        Icd entityTest = service.save(entityClone);

        assertEqualsEntity(entity, entityTest);

        verify(repository).existsByNameAndVersion(entityClone.getName(), entityClone.getVersion());
        verify(repository).save(entityClone);
    }

    @Test
    void update() {
        Icd entity = getNewEntity();
        Icd entityForTest = entity.toBuilder()
                .name(generator.nextObject(String.class))
                .version(11)
                .comment(generator.nextObject(String.class))
                .build();
        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entity.toBuilder().build());

        Icd entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getVersion(), entityTest.getVersion());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update_rnd() {
        Icd entity = generator.nextObject(Icd.class);
        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        when(repository.getOrThrow(entity.getId())).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void delete() {
        Icd entity = getNewEntity();
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

    private Icd getNewEntity() {
        Icd entity = generator.nextObject(Icd.class);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    private List<Icd> getNewEntityList(int count){
        List<Icd> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntity());
        }

        return entities;
    }

    private void assertEqualsEntity(Icd entity, Icd test){
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(Icd entity, Icd test){
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getName(), test.getName());
        assertEquals(entity.getVersion(), test.getVersion());
        assertEquals(entity.getComment(), test.getComment());
    }
}