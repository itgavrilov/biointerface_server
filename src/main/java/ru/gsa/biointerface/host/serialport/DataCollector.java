package ru.gsa.biointerface.host.serialport;

public interface DataCollector {
    void setDevice(int serialNumber, int amountChannels);

    void setSampleInChannel(int i, int value);

    void setFlagTransmission();
}
