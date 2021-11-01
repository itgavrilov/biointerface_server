package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Examination;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface CustomizedExaminationRepository {
    void insert(Examination examination) throws Exception;
    void transactionOpen() throws Exception;
    void transactionClose() throws Exception;
    boolean sessionIsOpen();
    boolean transactionIsOpen();
}
