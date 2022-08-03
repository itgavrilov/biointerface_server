package ru.gsa.biointerface.unit.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.dto.ChannelDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private ChannelRepository repository;
    @Mock
    private ChannelNameRepository channelNameRepository;

    @InjectMocks
    private ChannelService service;

    @Test
    void findAll() {
        List<Channel> entities = generator.objects(Channel.class, 5).toList();
        when(repository.findAllByExaminationIdAndChannelNameId(null, null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, null);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByExaminationIdAndChannelNameId(null, null);
    }

    @Test
    void findAll_empty() {
        when(repository.findAllByExaminationIdAndChannelNameId(null, null)).thenReturn(new ArrayList<>());

        List<Channel> entityTests1 = service.findAll(null, null);
        assertNotNull(entityTests1);
        assertIterableEquals(new ArrayList<>(), entityTests1);
        verify(repository).findAllByExaminationIdAndChannelNameId(null, null);
    }

    @Test
    void findAll_byExamination() {
        List<Channel> entities = generator.objects(Channel.class, 5).toList();
        Examination examination = generator.nextObject(Examination.class);
        entities.forEach(e -> e.setExamination(examination));
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), null);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), null);
    }

    @Test
    void findAll_byChannelName() {
        List<Channel> entities = generator.objects(Channel.class, 5).toList();
        ChannelName channelName = generator.nextObject(ChannelName.class);
        entities.forEach(e -> e.setChannelName(channelName));
        when(repository.findAllByExaminationIdAndChannelNameId(null, channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, channelName.getId());
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByExaminationIdAndChannelNameId(null, channelName.getId());
    }

    @Test
    void findAll_byExaminationAndChannelName() {
        List<Channel> entities = generator.objects(Channel.class, 5).toList();
        Examination examination = generator.nextObject(Examination.class);
        ChannelName channelName = generator.nextObject(ChannelName.class);
        entities.forEach(e -> {
            e.setExamination(examination);
            e.setChannelName(channelName);
        });
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), channelName.getId());
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId());
    }

    @Test
    void getById() {
        Channel entity = generator.nextObject(Channel.class);
        when(repository.getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber())).thenReturn(entity);

        Channel entityTest = service.getById(entity.getId().getExaminationId(), entity.getId().getNumber());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber());
    }

    @Test
    void update() {
        Channel entity = generator.nextObject(Channel.class);
        Channel entityNew = generator.nextObject(Channel.class);
        entityNew.setId(entity.getId());
        ChannelDTO dto = ChannelDTO.builder()
                .examinationId(entityNew.getId().getExaminationId())
                .number(entityNew.getId().getNumber())
                .channelNameId(entityNew.getChannelName().getId())
                .build();

        when(repository.getOrThrow(dto.getExaminationId(), dto.getNumber())).thenReturn(entity);
        when(channelNameRepository.getOrThrow(dto.getChannelNameId())).thenReturn(entityNew.getChannelName());

        Channel entityTest = service.update(dto);
        assertEquals(entityNew, entityTest);
        verify(repository).getOrThrow(dto.getExaminationId(), dto.getNumber());
        verify(channelNameRepository).getOrThrow(dto.getChannelNameId());
    }

    @Test
    void update_rnd() {
        ChannelDTO dto = generator.nextObject(ChannelDTO.class);
        UUID rnd1 = dto.getExaminationId();
        Byte rnd2 = dto.getNumber();
        String message = String.format(repository.MASK_NOT_FOUND, rnd1, rnd2);
        when(repository.getOrThrow(rnd1, rnd2)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rnd1, rnd2), message);
        verify(repository).getOrThrow(rnd1, rnd2);
    }

    @Test
    void save() {
        Channel entity = generator.nextObject(Channel.class);
        when(repository.save(entity)).thenReturn(entity);

        Channel entityTest = service.save(entity);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        Channel entity = generator.nextObject(Channel.class);
        UUID examinationId = entity.getId().getExaminationId();
        Byte number = entity.getId().getNumber();
        when(repository.getOrThrow(examinationId, number)).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(examinationId, number));
        verify(repository).getOrThrow(examinationId, number);
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        UUID examinationId = UUID.randomUUID();
        Byte number = (byte) generator.nextInt();
        String message = String.format(repository.MASK_NOT_FOUND, examinationId, number);
        when(repository.getOrThrow(examinationId, number)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(examinationId, number), message);
        verify(repository).getOrThrow(examinationId, number);
    }
}