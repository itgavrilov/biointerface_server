package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private final List<ConnectionHandler> connections = new ArrayList<>();

    public void scanningSerialPort() {
        connections.clear();
        List<SerialPort> serialPorts = getSerialPortsWithDevises();
        for (SerialPort serialPort : serialPorts) {
            try {
                ConnectionHandler connection = new ConnectionHandler(serialPort);
                connections.add(connection);
            } catch (Exception e) {
                LOGGER.error("Error connection to serialPort(SystemPortName={})", serialPort.getSystemPortName(), e);
            }
        }
        LOGGER.info("Scanning devices");
    }

    private List<SerialPort> getSerialPortsWithDevises() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()))
                .collect(Collectors.toList());
    }

    public List<Device> getDevices() {
        return connections.stream()
                .peek(o -> {
                    if (o.isConnected()) {
                        try {
                            o.disconnect();
                        } catch (Exception e) {
                            LOGGER.error("Device disconnect error", e);
                        }
                    }
                })
                .filter(ConnectionHandler::isAvailableDevice)
                .map(ConnectionHandler::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public Connection getConnection(Device device) {
        Connection connection = connections.stream()
                .filter(o -> device.equals(o.getDevice()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        LOGGER.info("Get available devices");

        return connection;
    }
}
