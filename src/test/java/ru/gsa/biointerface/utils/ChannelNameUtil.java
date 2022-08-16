package ru.gsa.biointerface.utils;

import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import ru.gsa.biointerface.domain.entity.ChannelName;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChannelNameUtil {

    private static final EasyRandom generator = new EasyRandom();

    @SneakyThrows
    public static ChannelName getChannelName() {
        ChannelName entity = generator.nextObject(ChannelName.class);
        sleep(10);

        return entity;
    }

    public static List<ChannelName> getChannelNames(int count) {
        List<ChannelName> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getChannelName());
        }

        return entities;
    }

    public static void assertEqualsChannelName(ChannelName entity, ChannelName test) {
        assertEqualsChannelNameLight(entity, test);
        Assertions.assertEquals(entity.getId(), test.getId());
    }

    public static void assertEqualsChannelNameLight(ChannelName entity, ChannelName test) {
        assertNotNull(entity);
        assertNotNull(test);
        Assertions.assertEquals(entity.getName(), test.getName());
        Assertions.assertEquals(entity.getComment(), test.getComment());
    }
}
