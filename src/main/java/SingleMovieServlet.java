import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movie")
public class SingleMovieServlet extends HttpServlet implements Parameters {
//    private static final long serialVersionUID = 1L;
//
//            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//                String loginUrl = "jdbc:mysql://localhost:3306/moviedb?characterEncoding=latin1&useConfigs=maxPerformance";
//
//                // set response mime type
//                response.setContentType("text/html");
//
//                // get the printwriter for writing response
//                PrintWriter out = response.getWriter();
//                String movieTitle = "Bigfoot"; //request.getParameter("movie");
//
//                out.println("<html>");
//                out.println("<head><title>Fabflix</title></head>");
//
//                try {
//                    Class.forName("com.mysql.jdbc.Driver").newInstance();
//                    // create database connection
//                    Connection connection = DriverManager.getConnection(loginUrl, Parameters.username, Parameters.password);
//            // declare statement
//            Statement statement = connection.createStatement();
//            // prepare query
//            String query = "SELECT * from movies\n " +
//                    "where title = \""+movieTitle+"\"";
//            // execute query
//            ResultSet resultSet = statement.executeQuery(query);
//
//            out.println("<body>");
//            out.println("<h1>Title: " + movieTitle + "</h1>");
//
//            while (resultSet.next()){
//                // get a star from result set
//                String title = resultSet.getString("title");
//                int year = resultSet.getInt("year");
//                String director = resultSet.getString("director");
//
//                out.println("<ul>");
//                out.println("<li>Year: " + year + "</li>");
//                out.println("<li>Director: " + director + "</li>");
//                out.println("<li>Genre: " + year + "</li>");
//                out.println("<li>Stars: " + year + "</li>");
//                out.println("<li>Rating: " + year + "</li>");
//                out.println("</ul>");
//            }
//            out.println("</body>");
//
//            resultSet.close();
//            statement.close();
//            connection.close();
//
//        } catch (Exception e) {
//            /*
//             * After you deploy the WAR file through tomcat manager webpage,
//             *   there's no console to see the print messages.
//             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
//             *
//             * To view the last n lines (for example, 100 lines) of messages you can use:
//             *   tail -100 catalina.out
//             * This can help you debug your program after deploying it on AWS.
//             */
//            e.printStackTrace();
//
//            out.println("<body>");
//            out.println("<p>");
//            out.println("Exception in doGet: " + e.getMessage());
//            out.println("</p>");
//            out.print("</body>");
//        }
//
//        out.println("</html>");
//        out.close();
//
//    }


}