package org.test.cameraMonitor.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "EVENT_IMAGE")
public class EventImage extends Image {

    public EventImage() {
        super();
    }
}
