package org.test.cameraMonitor.util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.entities.Event;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 17/03/2013
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUtils {

    public static void cleanUpEventRecords(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            org.hibernate.Query globalQuery = session.createQuery
                    ("FROM Event");
            List<Event> list = globalQuery.list();
            Iterator<Event> iter = list.iterator();
            while (iter.hasNext()){
                Event event = iter.next();
                if (event.getTimeEnded() == 0){
                    Query query = HibernateUtil.getSessionFactory().openSession().createQuery("SELECT MAX(Date) FROM EventImage WHERE event_id = :eventId");
                    query.setParameter("eventId", event.getID());
                    long result = (Long)query.uniqueResult();
                    event.setTimeEnded(result);
                    session.save(event);
                }
            }

        }
        catch (Exception e){
            System.out.print(e);
        }
        finally {
            tx.commit();
            session.close();
        }
    }

    public static long getTableSizeByName(String name){
        String sqlQuery = "SELECT round(((data_length + index_length) / 1024 / 1024),2) 'size'" +
                            "FROM information_schema.TABLES WHERE table_schema = 'test' AND TABLE_NAME = :tableName";
        BigDecimal result = (BigDecimal)HibernateUtil.getSessionFactory().openSession().createSQLQuery(sqlQuery).setParameter("tableName", name).uniqueResult();
        return result.longValue();
    }

}
