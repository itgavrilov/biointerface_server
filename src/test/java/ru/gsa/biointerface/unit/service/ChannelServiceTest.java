package ru.gsa.biointerface.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.service.ChannelService;

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
import static ru.gsa.biointerface.utils.ChannelNameUtil.getChannelName;
import static ru.gsa.biointerface.utils.ChannelUtil.assertEqualsChannel;
import static ru.gsa.biointerface.utils.ChannelUtil.assertEqualsChannelLight;
import static ru.gsa.biointerface.utils.ChannelUtil.getChannel;
import static ru.gsa.biointerface.utils.ChannelUtil.getChannels;
import static ru.gsa.biointerface.utils.ExaminationUtil.getExamination;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository repository;

    @InjectMocks
    private ChannelService service;

    @Test
    void findAll() {
        List<Channel> entities = getChannels(getExamination(), null, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(null, null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsChannel(entity, entityTest);
        });

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
        Examination examination = getExamination();
        List<Channel> entities = getChannels(examination, null, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsChannel(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), null);
    }

    @Test
    void findAll_byChannelName() {
        ChannelName channelName = getChannelName();
        List<Channel> entities = getChannels(getExamination(), channelName, 5);
        entities.forEach(e -> e.setChannelName(channelName));
        when(repository.findAllByExaminationIdAndChannelNameId(null, channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, channelName.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsChannel(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(null, channelName.getId());
    }

    @Test
    void findAll_byExaminationAndChannelName() {
        Examination examination = getExamination();
        ChannelName channelName = getChannelName();
        List<Channel> entities = getChannels(examination, channelName, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), channelName.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsChannel(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId());
    }

    @Test
    void getById() {
        Examination examination = getExamination();
        Channel entity = getChannel(examination, null, (byte) 1);
        when(repository.getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber())).thenReturn(entity);

        Channel entityTest = service.getById(entity.getId().getExaminationId(), entity.getId().getNumber());

        assertEqualsChannel(entity, entityTest);

        verify(repository).getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber());
    }

    @Test
    void save() {
        Examination examination = getExamination();
        Channel entity = getChannel(examination, null, (byte) 1);
        when(repository.save(entity)).thenReturn(entity);

        Channel entityTest = service.save(entity);

        assertEqualsChannel(entity, entityTest);

        verify(repository).save(entity);
    }

    @Test
    void update() {
        ChannelName channelName = getChannelName();
        Examination examination = getExamination();
        Channel entity = getChannel(examination, channelName, (byte) 1);
        Channel entityClone = entity.toBuilder().build();
        ChannelName channelNameForTest = getChannelName();
        Channel entityForTest = getChannel(examination, channelNameForTest, (byte) 1);

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entityClone);

        Channel entityTest = service.update(entityForTest);

        assertEqualsChannelLight(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getExamination(), entityTest.getExamination());

        assertNotEquals(entity.getChannelName(), entityTest.getChannelName());
        assertNotEquals(entity.getComment(), entityTest.getComment());
        assertNotEquals(entityForTest.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entityForTest.getModifyDate(), entityTest.getModifyDate());

        verify(repository).getOrThrow(entityForTest.getId());
    }

    @Test
    void update_rnd() {
        Examination examination = getExamination();
        String message = String.format(repository.MASK_NOT_FOUND, examination.getId(), 1);
        when(repository.getOrThrow(examination.getId(), (byte) 1)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(examination.getId(), (byte) 1), message);
        verify(repository).getOrThrow(examination.getId(), (byte) 1);
    }

    @Test
    void delete() {
        Examination examination = getExamination();
        Channel entity = getChannel(examination, null, (byte) 1);
        UUID examinationId = examination.getId();
        Byte number = entity.getId().getNumber();
        when(repository.getOrThrow(examinationId, number)).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(examinationId, number));
        verify(repository).getOrThrow(examinationId, number);
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        Examination examination = getExamination();
        Channel entity = getChannel(examination, null, (byte) 1);
        UUID examinationId = examination.getId();
        Byte number = 1;
        String message = String.format(repository.MASK_NOT_FOUND, examinationId, number);
        when(repository.getOrThrow(examinationId, number)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(examinationId, number), message);

        verify(repository).getOrThrow(examinationId, number);
    }
}