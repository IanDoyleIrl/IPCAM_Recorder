package org.test.cameraMonitor.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.RecordedImage;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static RecordedImage getImageFromId(int id){
        return (RecordedImage) HibernateUtil.getSessionFactory().openSession().get(RecordedImage.class, id);
    }

    public static Event getEventFromId(int id){
        return (Event) HibernateUtil.getSessionFactory().openSession().get(Event.class, id);
    }

}