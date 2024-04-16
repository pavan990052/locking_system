package Locking_design;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/Signupverify")
public class Signupverify extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String profileImage = request.getParameter("profileImage");
        String fullName = request.getParameter("fullname");
        String userName = request.getParameter("username");
        String email = request.getParameter("email");
        String mobileNumber = request.getParameter("mobilenumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmpassword");

        try {
            // JDBC URL, username, and password
            String url = "jdbc:mysql://localhost:3306/locking?user=root&password=pavan@9900";

            // Validate username
            if (!isValidUsername(userName)) {
                sendAlert(response, "Invalid Username. Please enter a username starting with a capital letter followed by characters and ending with a number.");
                return;
            }

            // Validate password
            if (!isValidPassword(password)) {
                sendAlert(response, "Invalid Password. Please enter a password containing at least one capital letter and one special character (@ or $) and ending with a number.");
                return;
            }

            // Check email existence
            if (isEmailExists(email)) {
                sendAlert(response, "Email already exists. Please use another email.");
                return;
            }

            // Check username existence
            if (isUserExists(userName)) {
                sendAlert(response, "Username already exists. Please use another username.");
                return;
            }

            // JDBC connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url);

            // SQL query to insert values into the table
            String query = "INSERT INTO locking.register(profileimage, fullname, username, email, mobilenumber, password, confirmpassword) VALUES (?, ?, ?, ?, ?, ?, ?)";

            // Prepare the statement
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, profileImage);
            ps.setString(2, fullName);
            ps.setString(3, userName);
            ps.setString(4, email);
            ps.setString(5, mobileNumber);
            ps.setString(6, password);
            ps.setString(7, confirmPassword);

            // Execute the query
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                // Insert successful
                sendAlert(response, "Registration successful.");
                RequestDispatcher rd = request.getRequestDispatcher("signin.html");
                rd.include(request, response);
            } else {
                // Insert failed
                sendAlert(response, "Registration failed. Please try again.");
                RequestDispatcher rd = request.getRequestDispatcher("Signup.html");
                rd.include(request, response);
            }

            // Close the resources
            ps.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to validate username
    private boolean isValidUsername(String username) {
        // Regular expression to match username pattern
        String regex = "^[A-Z].*[0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    // Method to validate password
    private boolean isValidPassword(String password) {
        // Regular expression to match password containing at least one capital letter and one special character
        // one special character, and ending with a number
        String regex = "^(?=.*[A-Z])(?=.*[@$])[a-zA-Z0-9@$]*[0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Method to check if email exists in the database
    private boolean isEmailExists(String email) {
        String url = "jdbc:mysql://localhost:3306/locking?user=root&password=pavan@9900";
        String query = "SELECT * FROM locking.register WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If ResultSet has next row, means email already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume email doesn't exist in case of any error
        }
    }

    // Method to check if username exists in the database
    private boolean isUserExists(String username) {
        String url = "jdbc:mysql://localhost:3306/locking?user=root&password=pavan@9900";
        String query = "SELECT * FROM locking.register WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If ResultSet has next row, means username already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume username doesn't exist in case of any error
        }
    }

    // Method to send JavaScript alert to client
    private void sendAlert(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println("alert('" + message + "');");
        out.println("</script>");
    }
}
