package org.test.cameraMonitor.emailEngine;

import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 09/02/2013
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class EmailManager implements Runnable {

    @Override
    public void run() {
        while (true){
            try{
                if (!GlobalAttributes.getInstance().getEmailQueue().isEmpty()){
                    Event event = GlobalAttributes.getInstance().getEmailQueue().remove();
                    this.handleEventForEmail(event);
                }
                Thread.sleep(10000);
            }
            catch (Exception e){
                System.out.print(e);
                //Thread.sleep(500);
            }
        }
    }

    private void handleEventForEmail(Event event) throws MessagingException {
        final String username = GlobalAttributes.getInstance().getSMTPUsername();
        final String password = GlobalAttributes.getInstance().getSMTPPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", GlobalAttributes.getInstance().getSMTPServer());
        props.put("mail.smtp.port", GlobalAttributes.getInstance().getSTMPPort());


        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("admin@security.com"));
            for (String s : GlobalAttributes.getInstance().getEmailAddresses()){
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(s));
            }
            String dateStamp = new java.util.Date(event.getTimeStarted()).toString();
            message.setSubject("New Event - " + dateStamp);

            // create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            //fill message
            messageBodyPart.setText("Name:          " + event.getName() + "\n" +
                                    "Id:            " + event.getID() + "\n" +
                                    "Time Started:  " + dateStamp + "\n" +
                                    "Camera:        " + event.getCamera().getName() + "\n");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            for (EventImage eventImage : event.getEventImages()){
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(event.getEventImages().iterator().next().getImageData(), "image/jpeg");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(String.valueOf(eventImage.getDate()));
                multipart.addBodyPart(messageBodyPart);
            }

            // Put parts in message
            message.setContent(multipart);

            // Send the message
            Transport.send( message );

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally{
            session.getTransport().close();
        }

    }

    public EmailManager(){
    }



}
