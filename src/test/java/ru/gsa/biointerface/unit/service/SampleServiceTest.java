package ru.gsa.biointerface.unit.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.service.SampleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private SampleRepository repository;

    @InjectMocks
    private SampleService service;

    @Test
    void findAllByExaminationIdAndChannelNumber() {
        Channel channel = generator.nextObject(Channel.class);
        List<Sample> entities = generator.objects(Sample.class, 5).toList();
        entities.forEach(e -> e.setChannel(channel));
        UUID examinationId = channel.getId().getExaminationId();
        byte number = channel.getId().getNumber();
        when(repository.findAllByExaminationIdAndChannelNumber(examinationId, number)).thenReturn(entities);

        List<Sample> entityTests = service.findAllByExaminationIdAndChannelNumber(examinationId, number);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByExaminationIdAndChannelNumber(examinationId, number);
    }

    @Test
    void findAllByExaminationIdAndChannelNumber_rnd() {
        UUID examinationId = UUID.randomUUID();
        byte number = (byte) generator.nextInt();
        when(repository.findAllByExaminationIdAndChannelNumber(examinationId, number)).thenReturn(new ArrayList<>());

        List<Sample> entityTests = service.findAllByExaminationIdAndChannelNumber(examinationId, number);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAllByExaminationIdAndChannelNumber(examinationId, number);
    }

    @Test
    void findAllByExaminationIdAndChannelNumber_null() {
        List<Sample> entityTests = service.findAllByExaminationIdAndChannelNumber(null, null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAllByChannel() {
        Channel channel = generator.nextObject(Channel.class);
        List<Sample> entities = generator.objects(Sample.class, 5).toList();
        entities.forEach(e -> e.setChannel(channel));
        when(repository.findAllByChannel(channel)).thenReturn(entities);

        List<Sample> entityTests = service.findAllByChannel(channel);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByChannel(channel);
    }

    @Test
    void findAllByChannel_rnd() {
        Channel channel = generator.nextObject(Channel.class);
        when(repository.findAllByChannel(channel)).thenReturn(new ArrayList<>());

        List<Sample> entityTests = service.findAllByChannel(channel);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAllByChannel(channel);
    }

    @Test
    void findAllByChannel_null() {
        when(repository.findAllByChannel(null)).thenReturn(new ArrayList<>());

        List<Sample> entityTests = service.findAllByChannel(null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAllByChannel(null);
    }

    @Test
    void transactionOpen() {
    }

    @Test
    void transactionClose() {
    }

    @Test
    void setSampleInChannel() {
    }

    @Test
    void transactionIsOpen() {
    }
}