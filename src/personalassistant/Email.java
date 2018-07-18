/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
https://stackoverflow.com/questions/25341198/javax-mail-authenticationfailedexception-is-thrown-while-sending-email-in-java
use this in case of problem
 */



/**
 *
 * @author Shreyas
 */
public class Email {
    public static boolean sendMail(String from,String password,String message,String to[]) {
        return EmailSender.sendMail(from, password,message, to);
    }
    public static boolean sendMail(String message,String to[]) {
        return EmailSender.sendMail("javamailsender01@gmail.com", "javaxmailjar", message, to);
    }
}
class EmailSender {
    public static boolean sendMail(String from,String password,String message,String to[]) {
        String host="smtp.gmail.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user",from);
        props.put("mail.smtp.password",password);
        props.put("mail.smtp.port",587);
        props.put("mail.smtp.auth", "true");
        //Session session = Session.getDefaultInstance(props,null);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        
        
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            for (int i=0;i<to.length;i++) {
                toAddress[i]=new InternetAddress(to[i]);
            }
            for (int i=0;i<to.length;i++) {
                mimeMessage.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            mimeMessage.setSubject("Mail using Java API");
            mimeMessage.setText(message);
            Transport transport = session.getTransport("smtp");
            transport.connect(host,from,password);
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
            transport.close();
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}