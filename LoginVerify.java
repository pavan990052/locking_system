package Locking_design;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/LoginVerify")
public class LoginVerify extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        PrintWriter writer = resp.getWriter();

        // Establish JDBC connection
        String url = "jdbc:mysql://localhost:3306?user=root&password=pavan@9900";
        String query = "SELECT * FROM locking.signin WHERE username=? AND password=?";

        try {
        	   Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Login successful
                sendAlert(resp, "Login Successful. Welcome " + rs.getString("name"));
                RequestDispatcher dis = req.getRequestDispatcher("Welcome.html");
                dis.include(req, resp);
            } else {
                // Invalid login
                sendAlert(resp, "Invalid Login Details. Please try again.");
                RequestDispatcher dis = req.getRequestDispatcher("signin.html");
                dis.include(req, resp);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
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
