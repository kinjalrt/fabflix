import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/top20")
public class MovieListServlet extends HttpServlet implements Parameters {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password
//         RequestDispatcher rd = request.getRequestDispatcher("index.html");

        // set response mime type
        response.setContentType("text/html");

        String loginurl = "jdbc:mysql://localhost:3306/moviedb?characterEncoding=latin1&useConfigs=maxPerformance";

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");


        try {
            out.println("<body>");

            out.println("<H2>Top 20 Best Rated Movies</H2>");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

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
            String q1 = "SELECT DISTINCT title, year, director, rating\n" +
                    "FROM movies as m, ratings as r\n" +
                    "WHERE m.id = r.movieId\n" +
                    "ORDER BY rating DESC\n" +
                    "LIMIT 20";
            ResultSet r1 = select.executeQuery(q1);


                out.println("<ol>");
            while (r1.next()) {
                //hyperlink to SingleMovie which sends title and ratings as a parameter
                out.println("<li><H4>" + "<a class=\"active\" href=\"movieServlet?title="
                        + r1.getString("title")+"&rating=" + r1.getFloat("rating")+"\">"
                        + r1.getString("title") +" </a>"+"</H4></li>");
                out.println("<ul>");
                out.println("<li>Year: " + r1.getInt("year")+" </li>");
                out.println("<li>Director: " + r1.getString("director")+ " </li>");
                out.println("<li>Rating: " + r1.getFloat("rating")+ " </li>");


                Statement s2 = connection.createStatement();
                String q2 ="SELECT title, name\n" +
                        "FROM movies as m, genres as g, genres_in_movies as gim\n" +
                        "WHERE  m.title = \""+r1.getString("title") +"\" AND m.id = gim.movieId AND gim.genreId = g.id\n" +
                        "LIMIT 3";
                ResultSet r2 = s2.executeQuery(q2);
                out.println("<li>Genre:<ul>");
                while(r2.next()) {
                    out.println("<li>" + r2.getString("name")+" </li>");
                }
                out.println("</ul></li>");
                r2.close();
                s2.close();

                Statement s3 = connection.createStatement();
                String q3 ="SELECT title, name\n" +
                        "FROM movies as m, stars_in_movies as sim, stars as s\n" +
                        "WHERE  m.title = \""+r1.getString("title")+"\" AND m.id = sim.movieId AND sim.starId = s.id\n" +
                        "LIMIT 3";
                ResultSet r3 = s3.executeQuery(q3);
                out.println("<li>Star:<ul>");
                while(r3.next()) {
                    String starName = r3.getString("name");
                    out.println("<li><a class=\"active\" href=\"SingleStarServlet?star="+starName+"\">" + r3.getString("name") +" </a></li>");
                }
                out.println("</ul></li>");
                r3.close();
                s3.close();

                out.println("</ul>");
            }
            out.println("</ol>");

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
