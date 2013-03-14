package org.test.cameraMonitor.streamingServer; /**
 * jipCam : The Java IP Camera Project
 * Copyright (C) 2005-2006 Jason Thrasher
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.RecordedStream;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet to simulate the MJPEG stream coming from the camera.  This allows
 * use of a "canned" stream on the disk.  The stream is "played" in a loop for
 * the HTTP client.  The client should behave as if it's a continuous stream.
 *
 * The CGI request supports modifications to FPS.  This allows the client to run
 * at much higher FPS than the camera actually supports.  The timing of the response
 * MJPEG frames is not very accurate - but good enough up to around 100 FPS on a 2 GHz
 * computer.
 *
 * @author Jason Thrasher
 */
public class StreamingServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String streamingMode = request.getParameter("mode");
        String cameraID = request.getParameter("cameraId");
        try{
            if (streamingMode.equals("live") & cameraID != null){
                StreamingUtils.handleLiveStreaming(response, request, (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, Integer.parseInt(cameraID)));
            }
            if (streamingMode.equals("recording")){
                String recordingId = request.getParameter("recordingId");
               // String cameraId
                RecordedStream recordedStream = (RecordedStream) HibernateUtil.getSessionFactory().openSession().get(RecordedStream.class, Integer.parseInt(recordingId));
                if (recordedStream != null){
                    StreamingUtils.handleRecordedStreaming(response, request, recordedStream);
                }
                else{
                    response.setStatus(404);
                }
            }
            if (streamingMode.equals("event")){
                String eventId = request.getParameter("eventId");
                Event event = (Event) HibernateUtil.getSessionFactory().openSession().get(Event.class, Integer.parseInt(eventId));
                if (event != null){
                    StreamingUtils.handleEventStreaming(response, request, event);
                }
                else{
                    response.setStatus(404);
                }
            }
        }
        catch (Exception e){
            response.setStatus(500);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}