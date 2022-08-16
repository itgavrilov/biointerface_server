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
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.service.IcdService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.gsa.biointerface.utils.IcdUtil.assertEqualsIcd;
import static ru.gsa.biointerface.utils.IcdUtil.assertEqualsIcdLight;
import static ru.gsa.biointerface.utils.IcdUtil.getIcd;
import static ru.gsa.biointerface.utils.IcdUtil.getIcds;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class IcdServiceTest {

    @Mock
    private IcdRepository repository;

    @InjectMocks
    private IcdService service;

    @Test
    void findAll() {
        List<Icd> entities = getIcds(5);
        when(repository.findAll()).thenReturn(entities);

        List<Icd> entityTests = service.findAll();

        entityTests.forEach(entityTest -> {
            Icd entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsIcd(entity, entityTest);
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
        List<Icd> entities = getIcds(15);
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
                assertEqualsIcd(entity, entityTest);
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
        Icd entity = getIcd(10);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Icd entityTest = service.getById(entity.getId());

        assertEqualsIcd(entity, entityTest);

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
        Icd entity = getIcd(10);
        Icd entityClone = entity.toBuilder().build();
        when(repository.existsByNameAndVersion(entityClone.getName(), entityClone.getVersion())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        Icd entityTest = service.save(entityClone);

        assertEqualsIcd(entity, entityTest);

        verify(repository).existsByNameAndVersion(entityClone.getName(), entityClone.getVersion());
        verify(repository).save(entityClone);
    }

    @Test
    void update() {
        Icd entity = getIcd(10);
        Icd entityForTest = getIcd(11);
        entityForTest.setId(entity.getId());
        entityForTest.setCreationDate(entity.getCreationDate());

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entity.toBuilder().build());

        Icd entityTest = service.update(entityForTest);
        assertEqualsIcdLight(entityForTest, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getVersion(), entityTest.getVersion());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update_rnd() {
        Icd entity = getIcd(10);
        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        when(repository.getOrThrow(entity.getId())).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void delete() {
        Icd entity = getIcd(10);
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