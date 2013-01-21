package org.test.cameraMonitor.realtimeServer;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;
import org.test.cameraMonitor.constants.GlobalAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 19/01/2013
 * Time: 00:38
 * To change this template use File | Settings | File Templates.
 */
public class RealtimeEventServlet
        extends HttpServlet implements CometProcessor {

    protected ArrayList<HttpServletResponse> connections = new ArrayList<HttpServletResponse>();
    protected MessageSender messageSender = null;

    public void init() throws ServletException {
        messageSender = new MessageSender();
        Thread messageSenderThread =
                new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
        messageSenderThread.setDaemon(true);
        messageSenderThread.start();
        GlobalAttributes.getInstance().getAttributes().put("realTimeEvent", messageSender);
    }

    public void destroy() {
        connections.clear();
        messageSender.stop();
        messageSender = null;
    }

    /**
     * Process the given Comet event.
     *
     * @param event The Comet event that will be processed
     * @throws IOException
     * @throws ServletException
     */
    public void event(CometEvent event)
            throws IOException, ServletException {
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        //GlobalAttributes.getInstance().getAttributes().put("realTimeEventResponse", response);
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
            synchronized (connections) {
                connections.add(response);
            }
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.END) {
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.READ) {
            InputStream is = request.getInputStream();
            byte[] buf = new byte[512];
            do {
                int n = is.read(buf); //can throw an IOException
                if (n > 0) {
                    System.out.println("Read " + n + " bytes: " + new String(buf, 0, n)
                            + " for session: " + request.getSession(true).getId());
                } else if (n < 0) {
                    //error(event, request, response);
                    return;
                }
            } while (is.available() > 0);
        }
    }

    public class MessageSender implements Runnable {

        protected boolean running = true;
        protected ArrayList<String> messages = new ArrayList<String>();

        public MessageSender() {
        }

        public void stop() {
            running = false;
        }

        /**
         * Add message for sending.
         */
        public void send(String message) {
            synchronized (messages) {
                messages.add(message);
                messages.notify();
            }
        }

        public void run() {

            while (running) {

                if (messages.size() == 0) {
                    try {
                        synchronized (messages) {
                            messages.wait();
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }

                synchronized (connections) {
                    String[] pendingMessages = null;
                    synchronized (messages) {
                        pendingMessages = messages.toArray(new String[0]);
                        messages.clear();
                    }
                    // Send any pending message on all the open connections
                    for (int i = 0; i < connections.size(); i++) {
                        try {
                            PrintWriter writer = connections.get(i).getWriter();
                            for (int j = 0; j < pendingMessages.length; j++) {
                                writer.println(pendingMessages[j] + "<br>");
                            }
                            writer.flush();
                        } catch (IOException e) {
                            log("IOExeption sending message", e);
                        }
                    }
                }

            }

        }

    }

}
