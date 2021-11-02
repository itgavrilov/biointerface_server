package ru.gsa.biointerface.repository.customized;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.exception.DeleteException;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.exception.TransactionNotOpenException;
import ru.gsa.biointerface.repository.exception.TransactionStopException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.LinkedList;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ExaminationRepositoryCustomImpl implements ExaminationRepositoryCustom {
    protected final Logger LOGGER = LoggerFactory.getLogger(ExaminationRepository.class);
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Examination insert(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        JpaEntityInformation<Examination, ?> entityInformation =
                JpaEntityInformationSupport.getEntityInformation(Examination.class, entityManager);

        try {
            if (entityInformation.isNew(entity)) {
                entityManager.persist(entity);
            } else {
                entity = entityManager.merge(entity);
            }
            LOGGER.info("Entity insert successful");
            return entity;
        } catch (Exception e) {
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    @Override
    public void transactionOpen() throws Exception {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            LOGGER.info("Transaction open is successful");
        } catch (Exception e) {
            LOGGER.error("Transaction opening error", e);
            throw new TransactionNotOpenException(e);
        }
    }

    @Override
    public void transactionClose() throws Exception {
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

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
    public boolean sessionIsOpen() {
        return entityManager != null && entityManager.isOpen();
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean transactionIsOpen() {
        return sessionIsOpen() && entityManager.getTransaction().isActive();
    }
}
