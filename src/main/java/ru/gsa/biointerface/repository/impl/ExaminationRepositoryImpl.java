package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.exception.ReadException;
import ru.gsa.biointerface.repository.exception.TransactionNotOpenException;
import ru.gsa.biointerface.repository.exception.TransactionStopException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ExaminationRepositoryImpl extends AbstractRepository<Examination, Long> implements ExaminationRepository {
    private Session session;

    @Autowired
    public ExaminationRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void insert(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        try {
            session.save(entity);
            LOGGER.info("Entity insert successful");
        } catch (Exception e) {
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    @Override
    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws Exception {
        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where patientRecord_id = :id";
            //noinspection unchecked
            Query<Examination> query = session.createQuery(hql);
            query.setParameter("id", patientRecord.getId());
            List<Examination> entities = query.list();
            LOGGER.info("Reading entities by patientRecord is successful");

            return entities;
        } catch (Exception e) {
            LOGGER.error("Error reading entities by patientRecord", e);
            throw new ReadException(e);
        }
    }

    @Override
    public void transactionOpen() throws Exception {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
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
            session.flush();
            session.getTransaction().commit();
            session.close();
            LOGGER.info("Transaction close is successful");
        } catch (Exception e) {
            LOGGER.error("Transaction closing error", e);
            throw new TransactionStopException(e);
        }
    }

    @Override
    public boolean sessionIsOpen() {
        return session != null && session.isOpen();
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean transactionIsOpen() {
        return sessionIsOpen() && session.getTransaction().isActive();
    }
}
