package service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogService {

    private static FileHandler fh;

    /**
     * Logs an exception as error into the log file.
     * @param e the exception to log
     */
    public static void log(Exception e) {
        Logger logger = getLogger();
        logger.severe(getStackTrace(e));
        fh.close();
    }

    /**
     * Logs a text as information into the log file.
     * @param text the text to log
     */
    public static void log(String text) {
        Logger logger = getLogger();
        logger.info(text);
        fh.close();
    }

    /**
     * Reads the stack trace of an exception and converts it to string.
     *
     * @param e the exception
     * @return the stack trace as string
     */
    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private static Logger getLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        InputStream is = null;
        Logger logger = null;
        try {
            String logFile = ConfigService.readFromConfig("log");
            logger = Logger.getLogger("ProduktionAnwesendZeiten");
            // set true to add the log to existing file
            fh = new FileHandler(logFile, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);
        } catch (Exception e) {
            // should not be called
            e.printStackTrace();
        }
        return logger;
    }

    public static void sendErrorMail(Exception e) {
        try {
            // Read data from config
            String smtpHost = ConfigService.readFromConfig("smtp.host");
            String fromMail = ConfigService.readFromConfig("from.mail");
            String fromName = ConfigService.readFromConfig("from.name");
            String toMailsTmp = ConfigService.readFromConfig("to.mails");
            String[] toMails = toMailsTmp.split(",");
            Properties properties = new Properties();
            // Add Server-address to properties
            properties.put("mail.smtp.host", smtpHost);
            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromMail, fromName));
            for(String toMail : toMails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
            }
            String direction = ConfigService.readFromConfig("direction");
            String subject = "Fehler beim Sichern der Stempelzeiten";
            message.setSubject(subject);
            message.setContent(getStackTrace(e), "text/plain");
            Transport.send(message);
            log("Mail über Exception versendet an: " + toMailsTmp);
        } catch (Exception ex) {
            log(ex);
        }
    }
}
