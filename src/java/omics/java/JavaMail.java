package org.omics.java;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */

public class JavaMail {
   public static void main(String[] args) {
      // Recipient's email ID needs to be mentioned.
      String to = "xxxx@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "xxxx@gmail.com";

      final String username = "xxxx";//change accordingly
      final String password = "yyy";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";
      String port = "587";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
  	  props.put("mail.transport.protocol", "smtps");
  	  props.put("mail.smtp.port", port);
  	  props.put("mail.smtp.socketFactory.fallback", "false");
  	  props.put("mail.smtp.socketFactory.port", port); 
  	  props.put("mail.smtp.ssl", "true");

      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("Testing Subject");

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText("This is message body");

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
//         messageBodyPart = new MimeBodyPart();
//         String filename = "C:/Users/Administrator/Desktop/jAnnovar/J Med Genet-2015-He-282-8.pdf";
//         DataSource source = new FileDataSource(filename);
//         messageBodyPart.setDataHandler(new DataHandler(source));
//         messageBodyPart.setFileName("result");
//         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart);

         // Send message
         Transport.send(message);

         System.out.println("Sent message successfully....");
  
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }

public static void confirmationMail(String to) {
      // Recipient's email ID needs to be mentioned.
//      String to = "xxxx@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "xxxx@gmail.com";

      final String username = "xxxx";//change accordingly
      final String password = "yyy";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";
      String port = "587";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
  	  props.put("mail.transport.protocol", "smtps");
  	  props.put("mail.smtp.port", port);
  	  props.put("mail.smtp.socketFactory.fallback", "false");
  	  props.put("mail.smtp.socketFactory.port", port); 
  	  props.put("mail.smtp.ssl", "true");

      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("Confirmation");

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText("This is message body");

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart);

         // Send message
         Transport.send(message);

         System.out.println("Sent message successfully....");
  
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }

public static void resultMail(String to, String folder, ArrayList<String> attachments) {
      // Recipient's email ID needs to be mentioned.
//      String to = "xxxx@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "xxxx@gmail.com";

      final String username = "xxxx";//change accordingly
      final String password = "yyy";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";
      String port = "587";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
  	  props.put("mail.transport.protocol", "smtps");
  	  props.put("mail.smtp.port", port);
  	  props.put("mail.smtp.socketFactory.fallback", "false");
  	  props.put("mail.smtp.socketFactory.port", port); 
  	  props.put("mail.smtp.ssl", "true");

      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("Annotation Result");

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText("This is message body");

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
//         for(String attachment : attachments){
//             messageBodyPart = new MimeBodyPart();
//             String filename = folder+"/"+attachment;
//             DataSource source = new FileDataSource(filename);
//             messageBodyPart.setDataHandler(new DataHandler(source));
//             messageBodyPart.setFileName(attachment);
//             multipart.addBodyPart(messageBodyPart);
//             System.out.println(filename);
//         }
         for(String attachment : attachments){
        	 if(attachment.endsWith(".annotated.txt")){
                 messageBodyPart = new MimeBodyPart();
                 String filename = folder+"/"+attachment;
                 DataSource source = new FileDataSource(filename);
                 messageBodyPart.setDataHandler(new DataHandler(source));
                 messageBodyPart.setFileName(attachment);
                 multipart.addBodyPart(messageBodyPart);
                 System.out.println(filename); 
        	 }
         }
         // Send the complete message parts
         message.setContent(multipart);

         // Send message
         Transport.send(message);

         System.out.println("Sent message successfully....");
  
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}