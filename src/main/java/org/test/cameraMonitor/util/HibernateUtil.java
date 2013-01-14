package org.test.cameraMonitor.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.test.cameraMonitor.entities.RecordedImage;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            System.out.println("Trying local xml file!");
            SessionFactory sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();

            return sessionFactory;
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

}