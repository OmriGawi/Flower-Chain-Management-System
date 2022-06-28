package emails;

import utils.Constants;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Handles emails sending
 */
public class EmailManager {
    /**
     * Send email from 'zerli.g16@gmail.com'
     * @param to email address to send the mail to
     * @param subject email's subject
     * @param titleText title inside the template
     * @param descriptionText description inside the template
     * @param htmlFileName html template file name
     * @return True - mail was sent successfully<br>False - otherwise
     */
    public static synchronized boolean sendEmail(String to, String subject, String titleText, String descriptionText, String htmlFileName) {
        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Get the default Session object.
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Constants.EMAIL_USERNAME, Constants.EMAIL_PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);

            InputStream inputStream = EmailManager.class.getResourceAsStream("htmls/" + htmlFileName);
            String htmlString = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            htmlString = htmlString
                    .replace("TITLE_TEXT", titleText)
                    .replace("DESCRIPTION_TEXT", descriptionText);
            message.setContent(htmlString, "text/html; charset=UTF-8");

            System.out.println("Sending email to '" + to + "' about '" + titleText + "' with format of '" + htmlFileName + "'");
            Transport.send(message);
            System.out.println("Email was sent to '" + to + "' about '" + titleText + "' with format of '" + htmlFileName + "'");

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
