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

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


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
        String pdfFileName = "test.pdf";
        String contextPath = getServletContext().getRealPath(File.separator);
        File pdfFile = new File(contextPath + pdfFileName);

        //response.setContentLength((int) pdfFile.length());
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        int bytes;
        while ((true)) {
            //responseOutputStream.write(b);
            DetachedCriteria maxQuery = DetachedCriteria.forClass( RecordedImage.class );
            maxQuery.setProjection( Projections.max("Id") );
            Criteria query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
            query.add( Property.forName("Id").eq( maxQuery ) );
            RecordedImage image = (RecordedImage) query.uniqueResult();
            //response.addHeader("content-length", String.valueOf(image.getImageData().length));
            //response.addHeader("content-type", "image/jpeg");
            responseOutputStream.write(("--myboundary").getBytes());
            responseOutputStream.write(("\r\n").getBytes());
            responseOutputStream.write(("Content-Type:image/jpeg").getBytes());
            responseOutputStream.write(("\r\n").getBytes());
            responseOutputStream.write(("Content-Length:" + image.getImageData().length).getBytes());
            responseOutputStream.write(("\r\n").getBytes());
            //responseOutputStream.write(empty.getBytes());
            //responseOutputStream.write(empty.getBytes());
            responseOutputStream.write(("\r\n").getBytes());
            responseOutputStream.write(image.getImageData());
            responseOutputStream.write(b);
            responseOutputStream.flush();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}