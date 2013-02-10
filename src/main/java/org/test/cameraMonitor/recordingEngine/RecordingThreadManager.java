package org.test.cameraMonitor.recordingEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.test.cameraMonitor.emailEngine.EmailManager;
import org.test.cameraMonitor.remoteStorage.AWS_S3StorageManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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

    private ExecutorService recordingExecutor;
    private ExecutorService S3Executor;
    private ExecutorService emailExecutor;
    Logger logger = LogManager.getLogger(RecordingThreadManager.class.getName());


    public void contextInitialized(ServletContextEvent sce) {
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        final List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        tasks.add(Executors.callable(new RecordingEngine()));
        tasks.add(Executors.callable(new AWS_S3StorageManager()));
        tasks.add(Executors.callable(new EmailManager()));
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
