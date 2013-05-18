package org.test.cameraMonitor.recordingEngine;

import org.test.cameraMonitor.util.DatabaseUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Ian
 * Date: 07/04/2013
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUtilManager implements ThreadManagerInterface {

    private boolean running = true;

    @Override
    public void shutdownThread() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running){
            DatabaseUtils.cleanUpEventRecords();
        }
        this.shutdownThread();
    }
}
