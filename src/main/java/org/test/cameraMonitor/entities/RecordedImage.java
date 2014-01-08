package org.test.cameraMonitor.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 11/01/2013
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "RECORDED_IMAGE")
public class RecordedImage extends Image {

    public RecordedImage() {
        super();
    }

    public RecordedImage(byte[] info){
        super();
        this.setImageData(info);
    }

}
