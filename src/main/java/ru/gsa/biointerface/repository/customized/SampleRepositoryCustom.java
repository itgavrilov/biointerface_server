package ru.gsa.biointerface.repository.customized;

import ru.gsa.biointerface.domain.entity.Sample;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface SampleRepositoryCustom {
    Sample insert(Sample examination) throws Exception;
    void transactionOpen() throws Exception;
    void transactionClose() throws Exception;
    boolean sessionIsOpen();
    boolean transactionIsOpen();
}
