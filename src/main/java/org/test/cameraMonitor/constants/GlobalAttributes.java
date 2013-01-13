package org.test.cameraMonitor.constants;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 13/01/2013
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class GlobalAttributes {
    private static GlobalAttributes ourInstance = new GlobalAttributes();
    private HashMap<String, Object> attributes = new HashMap<String, Object>();

    public static GlobalAttributes getInstance() {
        return ourInstance;
    }

    private GlobalAttributes() {
    }

    public HashMap<String, Object> getAttributes(){
        return this.attributes;
    }
}
