package org.test.cameraMonitor.entities;

import org.test.cameraMonitor.constants.PanMotion;
import org.test.cameraMonitor.constants.ZoomMotion;

import javax.persistence.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="CONTROLSYSTEM")
public class ControlSystem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int ID;

    @Column
    private String name;

    @Column
    private boolean canPan;

    @Column
    private HashMap<PanMotion, String> panControl;

    @Column
    private boolean canZoom;

    @Column
    private HashMap<ZoomMotion, String> zoomControl;

    public ControlSystem() {
    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanPan() {
        return canPan;
    }

    public void setCanPan(boolean canPan) {
        this.canPan = canPan;
    }

    public HashMap<PanMotion, String> getPanControl() {
        return panControl;
    }

    public void setPanControl(HashMap<PanMotion, String> panControl) {
        this.panControl = panControl;
    }

    public boolean isCanZoom() {
        return canZoom;
    }

    public void setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
    }

    public HashMap<ZoomMotion, String> getZoomControl() {
        return zoomControl;
    }

    public void setZoomControl(HashMap<ZoomMotion, String> zoomControl) {
        this.zoomControl = zoomControl;
    }
}
