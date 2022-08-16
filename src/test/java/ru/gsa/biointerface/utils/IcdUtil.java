package ru.gsa.biointerface.utils;

import org.jeasy.random.EasyRandom;
import ru.gsa.biointerface.domain.entity.Icd;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IcdUtil {

    private static final EasyRandom generator = new EasyRandom();

    public static Icd getIcd(int version) {
        Icd entity = generator.nextObject(Icd.class);
        entity.setVersion(version);
        try {
            sleep(10);
        } catch (Exception ignored) {
        }

        return entity;
    }

    public static List<Icd> getIcds(int count) {
        List<Icd> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getIcd(10));
        }

        return entities;
    }

    public static void assertEqualsIcd(Icd entity, Icd test) {
        assertEqualsIcdLight(entity, test);
        assertEquals(entity.getId(), test.getId());
    }

    public static void assertEqualsIcdLight(Icd entity, Icd test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getName(), test.getName());
        assertEquals(entity.getVersion(), test.getVersion());
        assertEquals(entity.getComment(), test.getComment());
    }
}
