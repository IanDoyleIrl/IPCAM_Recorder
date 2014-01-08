package org.test.cameraMonitor.entities;

import org.test.cameraMonitor.recordingEngine.IPCameraTest;
import org.test.cameraMonitor.util.ControlUtils;

import javax.persistence.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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

    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="CAMERA_CONTROLS", joinColumns=@JoinColumn(name="control_id"))
    private Map<String, String> controlUrls = new HashMap<String, String>(); // maps from attribute name to value

    @Column(length = 40)
    private String name;

    @Column
    private String url;

    @Column
    private boolean canControl;

    @Column
    private boolean active;

    @Column
    private boolean saveAllImages;
    //private ControlSystem control;

    @OneToMany(mappedBy="camera")
    private Set<Event> events;

    public boolean saveAllImages() {
        return saveAllImages;
    }

    public void setSaveAllImages(boolean saveAllImages) {
        this.saveAllImages = saveAllImages;
    }

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

    public boolean handleMovement(String controlOption) {
        return ControlUtils.sendControlCommand(this, this.controlUrls.get("up"));
    }

    public Exception isContactable(){
        HttpURLConnection connection = null;
        try{
            URL cam = new URL(this.getUrl());
            connection = (HttpURLConnection)cam.openConnection();
            IPCameraTest in = new IPCameraTest(connection.getInputStream());
        }
        catch(MalformedURLException malEx){
            return new MalformedURLException("Url not valid");
        }
        catch (IOException e) {
            return new MalformedURLException("Connection error");
        }
        finally {
            try{
                connection.disconnect();
            }
            catch(NullPointerException npEx){

            }
        }
        return null;
    }
}
