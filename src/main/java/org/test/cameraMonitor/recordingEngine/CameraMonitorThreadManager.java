package org.test.cameraMonitor.recordingEngine;

import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.util.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 04/03/2013
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class CameraMonitorThreadManager implements ThreadManagerInterface {

    HashMap<Integer, RecordingEngine> runningThreads = new HashMap<Integer, RecordingEngine>();
    private boolean running = true;

    public void shutdownThread(){
        this.running = false;
    }

    @Override
    public void run() {
        while (running){
            List<Camera> cameras = HibernateUtil.getSessionFactory().openSession().createQuery("FROM Camera").list();
            for (Camera c : cameras){
                if (c.isActive() && !runningThreads.containsKey(c.getID())){
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    RecordingEngine cameraThread = new RecordingEngine(c);
                    runningThreads.put(c.getID(), cameraThread);
                    executor.execute(cameraThread);

                }
                else if (!c.isActive() && runningThreads.containsKey(c.getID())){
                    RecordingEngine cameraThread = runningThreads.get(c.getID());
                    cameraThread.shutdownThread();
                    runningThreads.remove(cameraThread);

                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
