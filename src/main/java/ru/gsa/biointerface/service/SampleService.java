package ru.gsa.biointerface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.TransactionException;
import ru.gsa.biointerface.repository.SampleRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Cервис для работы с измерениями
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

    private final SampleRepository repository;

    @PostConstruct
    private void init() {
        log.debug("SampleService is init");
    }

    @PreDestroy
    private void destroy() {
        log.debug("SampleService is destruction");
    }

    /**
     * Получение массива измерений для канала исследования
     *
     * @param examinationId Идентификатор исследования {@link Examination#getId()}
     * @param channelNumber Номер канала {@link ChannelID#getNumber()}
     * @return Массива измерений {@link List<Sample>}
     */
    public List<Sample> findAllByExaminationIdAndChannelNumber(Integer examinationId, Integer channelNumber) {
        if (examinationId == null || channelNumber == null) {
            return new ArrayList<>();
        }

        return repository.findAllByExaminationIdAndChannelNumber(examinationId, channelNumber);
    }

    /**
     * Получение массива измерений для канала исследования
     *
     * @param channel Канал исследования
     * @return Массива измерений {@link List<Sample>}
     */
    public List<Sample> findAllByChannel(Channel channel) {
        return repository.findAllByChannel(channel);
    }

    public void transactionOpen() throws Exception {
        repository.transactionOpen();
        log.info("Transaction has been opened");
    }

    public void transactionClose() throws Exception {
        repository.transactionClose();
        log.info("Transaction has been closed");
    }

    public void setSampleInChannel(Channel channel, int value) throws Exception {
        if (!transactionIsOpen()) {
            log.warn("Transaction is close");
            throw new TransactionException("Recording not started");
        }
        if (channel == null) {
            throw new BadRequestException("Channel is null");
        }

        List<Sample> samples = channel.getSamples();
        Sample sample =
                new Sample(
                        samples.size(),
                        channel,
                        value
                );
        sample = repository.insert(sample);
        samples.add(samples.size(), sample);
    }

    public boolean transactionIsOpen() {
        return repository.transactionIsOpen();
    }
}
