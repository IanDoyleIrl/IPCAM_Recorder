package org.test.cameraMonitor.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="CAMERA")
public class Camera {

    @Id
    @Column(name = "CAMERA_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int ID;

    @Column(length = 40)
    private String name;

    @Column
    private String url;

    @Column
    private boolean canControl;

    @Column
    private boolean active;

    //private ControlSystem control;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "camera")
    private Set<Event> events;

    public int getID() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCanControl() {
        return canControl;
    }

    public void setCanControl(boolean canControl) {
        this.canControl = canControl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
