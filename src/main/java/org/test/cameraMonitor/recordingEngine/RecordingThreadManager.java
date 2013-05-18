package org.test.cameraMonitor.recordingEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.test.cameraMonitor.emailEngine.EmailManager;
import org.test.cameraMonitor.remoteStorage.AWS_S3StorageManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 10/01/2013
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class RecordingThreadManager implements ServletContextListener {

    final ExecutorService executor = Executors.newFixedThreadPool(3);
    Logger logger = LogManager.getLogger(RecordingThreadManager.class.getName());
    final List<ThreadManagerInterface> tasks = new ArrayList<ThreadManagerInterface>();

    public void contextInitialized(ServletContextEvent sce) {
        tasks.add((new CameraMonitorThreadManager()));
        tasks.add(new AWS_S3StorageManager());
        tasks.add(new EmailManager());
        tasks.add(new DatabaseUtilManager());
        for (ThreadManagerInterface c : tasks){
                executor.execute(c);
        }
        System.out.println("Done");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutdown called - killing all threads");
        for (ThreadManagerInterface t : tasks){
            t.shutdownThread();
        }
        executor.shutdownNow();
    }


}
