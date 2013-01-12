package org.test.cameraMonitor.recordingEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Servlet implementation class VideoStreamServlet
 */

public class VideoStreamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public VideoStreamServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String range = request.getHeader("range");
        String browser = request.getHeader("User-Agent");
        System.out.println(browser);
        if(browser.indexOf("Firefox") != -1){
            System.out.println("==========ITS FIREFOX=============");
            byte[] data = getBytesFromFile(new File("D:/media/final.ogg"));
            response.setContentType("video/ogg");
            response.setContentLength(data.length);
            response.setHeader("Content-Range", range + Integer.valueOf(data.length-1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            byte[] content = new byte[1024];
            BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(data));
            OutputStream os = response.getOutputStream();
            while (is.read(content) != -1) {
                //System.out.println("... write bytes");
                os.write(content);
            }
            is.close();
            os.close();
        }

        else if(browser.indexOf("Chrome") != -1){
            System.out.println("==========ITS Chrome=============");
            byte[] data = getBytesFromFile(new File("D:/media/final.mp4"));
            String diskfilename = "final.mp4";
            response.setContentType("video/mp4");
            //response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"" );
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", range + Integer.valueOf(data.length-1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            byte[] content = new byte[1024];
            BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(data));
            OutputStream os = response.getOutputStream();
            while (is.read(content) != -1) {
                //System.out.println("... write bytes");
                os.write(content);
            }
            is.close();
            os.close();
        }

        else if(browser.indexOf("MSIE") != -1) {
            System.out.println("==========ITS IE9=============");
            byte[] data = getBytesFromFile(new File("D:/media/final.mp4"));
            String diskfilename = "final.mp4";
            response.setContentType("video/mpeg");
            //response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"" );
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", range + Integer.valueOf(data.length-1));
            response.setHeader("Accept-Ranges", "text/x-dvi");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            byte[] content = new byte[1024];
            BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(data));
            OutputStream os = response.getOutputStream();
            while (is.read(content) != -1) {
                //System.out.println("... write bytes");
                os.write(content);
            }
            is.close();
            os.close();
        }
        else if( browser.indexOf("CoreMedia") != -1) {
            System.out.println("============ Safari=============");
            byte[] data = getBytesFromFile(new File("D:/media/final.mp4"));
            String diskfilename = "final.mp4";
            response.setContentType("video/mpeg");
            //response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"" );
            System.out.println("data.length " + data.length);
            //response.setContentLength(data.length);
            //response.setHeader("Content-Range", range + Integer.valueOf(data.length-1));
            // response.setHeader("Accept-Ranges", " text/*, text/html, text/html;level=1, */* ");
            // response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            byte[] content = new byte[1024];
            BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(data));
            OutputStream os = response.getOutputStream();
            while (is.read(content) != -1) {
                //System.out.println("... write bytes");
                os.write(content);
            }
            is.close();
            os.close();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
    private static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        //System.out.println("\nDEBUG: FileInputStream is " + file);
        // Get the size of the file
        long length = file.length();
        //System.out.println("DEBUG: Length of " + file + " is " + length + "\n");
        /*
         * You cannot create an array using a long type. It needs to be an int
         * type. Before converting to an int type, check to ensure that file is
         * not loarger than Integer.MAX_VALUE;
         */
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large to process");
            return null;
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ( (offset < bytes.length)
                &&
                ( (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) ) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

}
