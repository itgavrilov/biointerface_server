package ru.gsa.biointerface;

import org.jeasy.random.EasyRandom;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class TestUtils {

    private static final EasyRandom generator = new EasyRandom();

    public static ChannelName getNewChannelName() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static Icd getNewIcd(int version) {
        Icd entity = generator.nextObject(Icd.class);
        entity.setVersion(version);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static Device getNewDevice(int amountChannels) {
        Device entity = generator.nextObject(Device.class);
        entity.setAmountChannels((byte) amountChannels);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static Patient getNewPatient(Icd icd, int minusDays) {
        Patient entity = generator.nextObject(Patient.class);
        entity.setBirthday(LocalDate.now().minusDays(minusDays));
        entity.setIcd(icd);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static Examination getNewExamination(Patient patient, Device device) {
        Examination entity = generator.nextObject(Examination.class);
        entity.setDatetime(LocalDateTime.now());
        entity.setDeviceId(device.getId());
        entity.setPatientId(patient.getId());
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static List<Channel> getListChannels(byte count) {
        List<Channel> channels = generator.objects(Channel.class, count).collect(Collectors.toList());
        for (byte i = 0; i < channels.size(); i++) {
            channels.get(i).setNumber(i);
            channels.get(i).setChannelName(null);
            channels.get(i).setSamples(new ArrayList<>());
            channels.get(i).setExamination(null);
        }

        return channels;
    }

    public static Channel getNewChannel(Examination examination, ChannelName channelName, Byte number) {
        Channel channel = generator.nextObject(Channel.class);
        channel.setNumber(number);
        channel.setExamination(examination);
        channel.setChannelName(channelName);

        return channel;
    }
}
