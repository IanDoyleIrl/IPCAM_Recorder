package org.test.cameraMonitor.entities;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.constants.EventType;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table (name = "EVENT")
public class Event {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int ID;

    @Column(unique=true, nullable = false, length=50)
    private String name;

    @Column(nullable = false)
    private long timeStarted;

    @Column(nullable = true)
    private long timeEnded;

    @Column(nullable = true)
    private String comments;

    @Column(nullable = true)
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name="camera_id")
    private Camera camera;

    @OneToMany(mappedBy="event")
    private Set<EventImage> eventImages = new HashSet<EventImage>();

    public Event() {
        //this.timeStarted = timeStarted;
    }

    public long getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public long getTimeEnded() {
        return timeEnded;
    }

    public void setTimeEnded(long timeEnded) {
        this.timeEnded = timeEnded;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Set<EventImage> getEventImages() {
        return eventImages;
    }

    public void setEventImages(Set<EventImage> eventImages) {
        this.eventImages = eventImages;
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
