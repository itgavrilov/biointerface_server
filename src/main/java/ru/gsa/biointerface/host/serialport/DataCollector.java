package ru.gsa.biointerface.host.serialport;

public interface DataCollector {

    void setDevice(String serialNumber, byte amountChannels);

    void setSampleInChannel(int i, int value);

    void setFlagTransmission();
}
