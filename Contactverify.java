package Locking_design;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Contactverify")
public class Contactverify extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstname = request.getParameter("first_name");
        String lastname = request.getParameter("last_name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        PrintWriter writer = response.getWriter();

        try {
            // JDBC URL, username, and password
            String url = "jdbc:mysql://localhost:3306/locking?user=root&password=pavan@9900";

            // SQL query to insert values into the table
            String query = "INSERT INTO locking.contactus (first_name, last_name, email, subject, message) VALUES (?, ?, ?, ?, ?)";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url);

            // Prepare the statement
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, email);
            ps.setString(4, subject);
            ps.setString(5, message);

            // Execute the query
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                // Insert successful
                RequestDispatcher rd = request.getRequestDispatcher("Welcome.html");
                rd.include(request, response);

                // Send thank you email to user
                sendThankYouEmail(firstname, lastname, email, subject, message, email);
                
                // Send form submission details to your email
                sendFormSubmissionDetails(firstname, lastname, email, subject, message);
            } else {
                // Insert failed
                RequestDispatcher rd = request.getRequestDispatcher("About.html");
                rd.include(request, response);
            }

            // Close the resources
            ps.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendThankYouEmail(String firstname, String lastname, String email, String subject, String message, String recipientEmail) {
        // SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Sender's email address and password
        String from = "pavankumar1042@gmail.com"; // Replace with your Gmail address
        String password = "bbxv wjqm kshr kwqm"; // Replace with your Gmail password

        // Recipient's email address
        String to = recipientEmail;

        // Get the Session object
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage mimeMessage = new MimeMessage(session);

            // Set From: header field
            mimeMessage.setFrom(new InternetAddress(from));

            // Set To: header field
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            mimeMessage.setSubject("Thank You for Contacting Us");

            // Set the actual message
            String emailContent = "Dear " + firstname + " " + lastname + ",\n\n";
            emailContent += "Thank you for contacting us. We have received your message and will get back to you shortly.\n\n";
            emailContent += "Here  the details you submitted:\n";
            emailContent += "Subject: " + subject + "\n";
            emailContent += "Message: " + message + "\n";
            emailContent += "Email:   " + email   + "\n";

            mimeMessage.setText(emailContent);

            // Send message
            Transport.send(mimeMessage);

            System.out.println("Thank you email sent successfully to user.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendFormSubmissionDetails(String firstname, String lastname, String email, String subject, String message) {
        // SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Sender's email address and password
        String from = "pavankumar1042@gmail.com"; // Replace with your Gmail address
        String password = "bbxv wjqm kshr kwqm"; // Replace with your Gmail password

        // Recipient's email address
        String to = "pavankumar1042@gmail.com"; // Your email address to receive form submission details

        // Get the Session object
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage mimeMessage = new MimeMessage(session);

            // Set From: header field
            mimeMessage.setFrom(new InternetAddress(from));

            // Set To: header field
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            mimeMessage.setSubject("New Contact Us Form Submission");

            // Set the actual message
            String emailContent = "New Contact Us Form Submission:\n\n";
            emailContent += "First Name: " + firstname + "\n";
            emailContent += "Last Name: " + lastname + "\n";
            emailContent += "Email: " + email + "\n";
            emailContent += "Subject: " + subject + "\n";
            emailContent += "Message: " + message + "\n";

            mimeMessage.setText(emailContent);

            // Send message
            Transport.send(mimeMessage);

            System.out.println("Form submission details sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
