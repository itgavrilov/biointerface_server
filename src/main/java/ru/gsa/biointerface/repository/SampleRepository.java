package ru.gsa.biointerface.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.domain.entity.SampleID;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.database.DataSource;
import ru.gsa.biointerface.repository.exception.ReadException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class SampleRepository extends AbstractRepository<Sample, SampleID> {
    @Autowired
    public SampleRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Sample> getAllByGraph(Channel channel) throws Exception {
        List<Sample> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where channel_id = :channel_id and examination_id = :examination_id";
            //noinspection unchecked
            Query<Sample> query = session.createQuery(hql);
            query.setParameter("channel_id", channel.getId());
            query.setParameter("examination_id", channel.getExamination().getId());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new ReadException(e);
        }

        return entities;
    }
}