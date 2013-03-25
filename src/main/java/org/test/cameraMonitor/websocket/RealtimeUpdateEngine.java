package org.test.cameraMonitor.websocket;

import org.test.cameraMonitor.util.APIUtils;

import java.nio.CharBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 24/03/2013
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class RealtimeUpdateEngine implements Runnable{
    private RealtimeUpdateServlet.UpdateMessageInbound servlet;

    public RealtimeUpdateEngine(RealtimeUpdateServlet.UpdateMessageInbound chatMessageInbound) {
        this.servlet = chatMessageInbound;
    }

    @Override
    public void run() {
        while (true){
            CharBuffer charBuffer = CharBuffer.wrap(String.valueOf(Math.random()).toCharArray());
            try {
                this.servlet.broadcast(APIUtils.getUpdateJSON());
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
