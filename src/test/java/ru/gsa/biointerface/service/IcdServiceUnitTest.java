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
import ru.gsa.biointerface.dto.IcdDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

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
class IcdServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private IcdRepository repository;

    @InjectMocks
    private IcdService service;

    @Test
    void findAll() {
        List<Icd> entities = generator.objects(Icd.class, 5).toList();
        when(repository.findAll()).thenReturn(entities);

        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<Icd> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<Icd> entities = generator.objects(Icd.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Icd> pageList = entities.subList(start, end);
            Page<Icd> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<Icd> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            verify(repository).findAll(pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Icd> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        Page<Icd> entityPageTests1 = service.findAll(pageable);
        assertNotNull(entityPageTests1);
        assertEquals(entityPage, entityPageTests1);
        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        Icd entity = generator.nextObject(Icd.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Icd entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
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
        IcdDTO dto = generator.nextObject(IcdDTO.class);
        Icd entity = new Icd(
                dto.getId(),
                dto.getName(),
                dto.getVersion(),
                dto.getComment(),
                new ArrayList<>());
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        Icd entityTest = service.saveOrUpdate(dto);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).findById(entity.getId());
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        Icd entity = generator.nextObject(Icd.class);
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