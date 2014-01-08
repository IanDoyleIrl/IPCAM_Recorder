package org.test.cameraMonitor.entities;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class Image {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int Id;

    @Column(nullable = false)
    private long Date;

    @Column (nullable = false, length = 100000)
    private byte[] imageData;

    @OneToOne
    private Camera camera;


    public Image() {
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setDate(long date) {
        Date = date;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public int getId() {
        return Id;
    }

    public long getDate() {
        return Date;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void save(){
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.save(this);
            tx.commit();
            session.close();
        }
        catch (Exception e){

        }
    }

}
