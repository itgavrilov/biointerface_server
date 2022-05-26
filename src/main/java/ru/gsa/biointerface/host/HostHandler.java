package ru.gsa.biointerface.host;

import ru.gsa.biointerface.domain.ChannelName;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Patient;
import ru.gsa.biointerface.host.cash.DataListener;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface HostHandler {
    void setPatientRecord(Patient patient);

    int getAmountChannels();

    void setNameInChannel(int numberOfChannel, ChannelName channelName);

    void setListenerInChannel(int numberOfChannel, DataListener listener);

    void connect();

    void disconnect() throws Exception;

    boolean isConnected();

    boolean isAvailableDevice();

    Device getDevice();

    void transmissionStart() throws Exception;

    void transmissionStop() throws Exception;

    boolean isTransmission();

    void controllerReboot() throws Exception;

    void recordingStart() throws Exception;

    void recordingStop() throws Exception;

    boolean isRecording();

    void setCommentForExamination(String comment) throws Exception;
}