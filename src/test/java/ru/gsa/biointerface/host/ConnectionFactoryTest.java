package ru.gsa.biointerface.host;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.SpringConfig;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.List;

import static java.lang.Thread.sleep;

class ConnectionFactoryTest {

    @Test
    void getDevices() {
        try(AnnotationConfigApplicationContext context
                    = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            ConnectionFactory connectionFactory = context.getBean(ConnectionFactory.class);

            try {
                connectionFactory.scanningSerialPort();
                sleep(1000);
                List<Device> serialPorts = connectionFactory.getDevices();
                System.out.println(serialPorts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}