package ru.gsa.biointerface.utils;

import org.jeasy.random.EasyRandom;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChannelUtil {

    private static final EasyRandom generator = new EasyRandom();

    public static Channel getChannel(Examination examination, ChannelName channelName, Byte number) {
        Channel channel = generator.nextObject(Channel.class);
        channel.setNumber(number);
        channel.setExamination(examination);
        channel.setChannelName(channelName);

        return channel;
    }

    public static List<Channel> getChannels(byte count) {
        return getChannels(null, null, count);
    }

    public static List<Channel> getChannels(Examination examination, ChannelName channelName, int count) {
        List<Channel> channels = generator.objects(Channel.class, count).collect(Collectors.toList());

        for (byte i = 0; i < channels.size(); i++) {
            channels.get(i).setNumber(i);
            channels.get(i).setChannelName(channelName);
            channels.get(i).setExamination(examination);
            channels.get(i).setSamples(new ArrayList<>());
        }

        return channels;
    }

    public static void assertEqualsChannel(Channel entity, Channel test) {
        assertEqualsChannelLight(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getExamination(), test.getExamination());
        assertIterableEquals(entity.getSamples(), test.getSamples());
    }

    public static void assertEqualsChannelLight(Channel entity, Channel test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getChannelName(), test.getChannelName());
        assertEquals(entity.getComment(), test.getComment());
    }
}
