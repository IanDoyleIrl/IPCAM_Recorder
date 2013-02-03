package org.test.cameraMonitor.recordingEngine;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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

    private ExecutorService executor;
    Logger logger = LogManager.getLogger(RecordingThreadManager.class.getName());


    public void contextInitialized(ServletContextEvent sce) {
        if (logger.isDebugEnabled()) {
            logger.debug('1');
        }
        Appender logFile = logger.getAppender("fileAppender");

        if (logger.isInfoEnabled()) {
            logger.info('2');
        }
        logger.info("Starting recording engine thread");
        executor = Executors.newSingleThreadExecutor();
        executor.submit(new RecordingEngine()); // Task should implement Runnable.

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
