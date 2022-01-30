/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.signup;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author mevir
 */
public class SignupServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("username");
        String email = request.getParameter("mail");
        String password = request.getParameter("password");

        String otp = createOtp();

        mailOtp(otp, name, email);
        HttpSession verificationSession = request.getSession();
        verificationSession.setAttribute("otp", otp);

        System.out.println(otp);
        response.sendRedirect("verify.html");

    }

    private String createOtp() {
        String alpha = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder otp = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = (int) (alpha.length() * Math.random());
            otp.append(alpha.charAt(index));
        }

        return otp.toString();
    }

    private void mailOtp(String otp, String name, String email) {
        // Create variables - to, from and host
        String to = email;
        String from = "xxxxxx@gmail.com";
        String host = "smtp.gmail.com";

        // Create properties object using System.getProperties()
        Properties properties = System.getProperties();

        // put mail.smtp.auth, true for authentication
        properties.put("mail.smtp.auth", "true");

        // put mail.smtp.starttls.enable, true for security
        properties.put("mail.smtp.starttls.enable", "true");

        // set property of mail.smtp.host, host for host
        properties.setProperty("mail.smtp.host", host);

        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create session object and get default instance
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "xxxxxxxxxxxxxxxx");
            }
        });

        // set session debug as true, to find bugs, not mandatory
        session.setDebug(true);

        // Compose message
        // In try block
        try {
            // create MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // set from
            message.setFrom(new InternetAddress(from));

            // add recipient - RecipientType to TO
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // set subject
            message.setSubject("ServletMail");

            // set text
//            message.setText("Hello "+ name + ", This message is from ServletMail.");
            message.setContent("<p>Hello <strong>" + name + "</strong>, This message is from ServletMail.<p>"
                    + "<h2>Code: " + otp + "</h2>"
                    + "<strong>The otp will be valid for 3 minutes. Do not share it with anyone.</strong>",
                    "text/html");

            // send message using Transport class
            Transport.send(message);
            System.out.println("Message sent successfully!");

        } catch (MessagingException e) {
            // catch MessagingException
            e.printStackTrace();
        }
    }

}
