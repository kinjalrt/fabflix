import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/SingleStarServlet")
public class SingleStarServlet extends HttpServlet implements Parameters {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password

        // set response mime type
        response.setContentType("text/html");
      //  String loginurl = "jdbc:mysql://localhost:3306/moviedb?characterEncoding=latin1&useConfigs=maxPerformance";

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        String singleStar = request.getParameter("star");

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            out.println("<body>");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String loginurl = "jdbc:mysql://localhost:3306/moviedb?characterEncoding=latin1&useConfigs=maxPerformance";


            // Connect to the test database
            Connection connection = DriverManager.getConnection(loginurl,
                    Parameters.username, Parameters.password);
            //  out.println("<body>i own you</body>");
            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();
            }

            // Create an execute an SQL statement to select all of table"Stars" records
            Statement select = connection.createStatement();
            String q1 = "SELECT id, name, birthYear\n" +
                    "FROM stars\n" +
                    "where name = \""+singleStar+"\"";
            ResultSet r1 = select.executeQuery(q1);


            while (r1.next()) {
                String id = r1.getString("id");
                out.println("<H2>Name: " + r1.getString("name")+"</H2>");
                out.println("<ul>");
                if (r1.getInt("birthYear")==0)
                    out.println("<li>Birth Year: N/A </li>");
                else
                    out.println("<li>Birth Year: " + r1.getInt("birthYear")+"</li>");

                Statement s2 = connection.createStatement();
                String q2 ="SELECT title\n" +
                        "FROM stars_in_movies as sim, movies as m\n" +
                        "WHERE sim.movieId = m.id AND sim.starId = \""+id+"\"";
                ResultSet r2 = s2.executeQuery(q2);
                while(r2.next()) {
                    out.println("<li>Movie: " + "<a class=\"active\" href=\"movieServlet?title="
                            + r2.getString("title")+"\">" + r2.getString("title")+" </a></li>");
                }
                r2.close();
                s2.close();
                out.println("</ul>");

            }

            r1.close();
            select.close();
            connection.close();



        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            e.printStackTrace();

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }
        out.print("</body>");
        out.println("</html>");
        out.close();

    }


}
