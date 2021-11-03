package ru.gsa.biointerface.repository.customized;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.exception.TransactionNotOpenException;
import ru.gsa.biointerface.repository.exception.TransactionStopException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class SampleRepositoryCustomImpl implements SampleRepositoryCustom {
    protected final Logger LOGGER = LoggerFactory.getLogger(SampleRepository.class);
    private EntityManager entityManager;
    private boolean transactionIsOpen;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Sample insert(Sample entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        JpaEntityInformation<Sample, ?> entityInformation =
                JpaEntityInformationSupport.getEntityInformation(Sample.class, entityManager);

        try {
            if (entityInformation.isNew(entity)) {
                entityManager.persist(entity);
            } else {
                entity = entityManager.merge(entity);
            }
            return entity;
        } catch (Exception e) {
            LOGGER.error("Insert sample error", e);
            throw new InsertException(e);
        }
    }

    @Override
    public void transactionOpen() throws Exception {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            LOGGER.info("Transaction open is successful");
            transactionIsOpen = true;
        } catch (Exception e) {
            LOGGER.error("Transaction opening error", e);
            throw new TransactionNotOpenException(e);
        }
    }

    @Override
    public void transactionClose() throws Exception {
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        transactionIsOpen = false;

        try {
            entityManager.flush();
            entityManager.getTransaction().commit();
            entityManager.close();
            entityManager = null;
            LOGGER.info("Transaction close is successful");
        } catch (Exception e) {
            LOGGER.error("Transaction closing error", e);
            throw new TransactionStopException(e);
        }
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean transactionIsOpen() {
        boolean result = entityManager != null &&
                entityManager.isOpen() &&
                entityManager.getTransaction().isActive();

        return result && transactionIsOpen;
    }
}
