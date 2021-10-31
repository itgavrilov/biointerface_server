package ru.gsa.biointerface.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.ReadException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ChannelRepository extends AbstractRepository<Channel, ChannelID> {
    @Autowired
    public ChannelRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Channel> getAllByExamination(Examination examination) throws Exception {
        List<Channel> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM channel where examination_id  = :id";
            //noinspection unchecked
            Query<Channel> query = session.createQuery(hql);
            query.setParameter("id", examination.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new ReadException(e);
        }

        return entities;
    }
}