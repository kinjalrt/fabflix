import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movieServlet")
public class SingleMovieServlet extends HttpServlet implements Parameters {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?characterEncoding=latin1&useConfigs=maxPerformance";

        // set response mime type
        response.setContentType("text/html");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        String movieTitle = request.getParameter("title");
        String movieRating = request.getParameter("rating");

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, Parameters.username, Parameters.password);
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query
            String query = "SELECT * from movies\n " +
                    "where title = \""+movieTitle+"\"";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);

            out.println("<body>");
            out.println("<h2>Title: " + movieTitle + "</h2>");

            while (resultSet.next()){
                // get a movie from result set
                String id = resultSet.getString("id");
                int year = resultSet.getInt("year");
                String director = resultSet.getString("director");

                out.println("<ul>");
                out.println("<li>Year: " + year + "</li>");
                out.println("<li>Director: " + director + "</li>");

                // declare genre statement
                Statement genreStatement = connection.createStatement();
                //prepare genre query
                String genreQuery = "SELECT g.name \n" +
                        "FROM moviedb.movies as m, moviedb.genres as g, moviedb.genres_in_movies as gm \n" +
                        "where title = \""+movieTitle+"\" and m.id = gm.movieId and gm.genreId = g.id;";
                //execute genre query
                ResultSet genreResultSet = genreStatement.executeQuery(genreQuery);
                out.println("<li>Genre: ");
                out.println("<ul>");
                while(genreResultSet.next()) {
                    String genre = genreResultSet.getString("name");
                    out.print("<li>" + genre + "</li>");
                }
                out.print("</ul></li>");
                genreResultSet.close();
                genreStatement.close();

                // declare stars statement
                Statement starsStatement = connection.createStatement();
                //prepare stars query
                String starsQuery = "SELECT s.name \n" +
                        "FROM moviedb.movies as m, moviedb.stars as s, moviedb.stars_in_movies as sm \n" +
                        "where title = \""+movieTitle+"\" and m.id = sm.movieId and sm.starId = s.id;";
                //execute stars query
                ResultSet starsResultSet = starsStatement.executeQuery(starsQuery);
                out.println("<li>Stars: ");
                out.println("<ul>");
                while(starsResultSet.next()) {
                    String stars = starsResultSet.getString("name");
                    out.print("<li><a class=\"active\" href=\"SingleStarServlet?star="+stars+"\">" + stars +" </a></li>");
                }
                out.print("</ul></li>");
                starsResultSet.close();
                starsStatement.close();

                if(movieRating == null) {
                    //declare ratings statement
                    Statement ratingStatement = connection.createStatement();
                    //prepare ratings query
                    String ratingQuery = "SELECT rating\n" +
                            "FROM moviedb.movies as m, moviedb.ratings as r " +
                            "where m.id = \"" + id + "\" and m.id = r.movieId";
                    //execute stars query
                    ResultSet ratingResultSet = ratingStatement.executeQuery(ratingQuery);
                    out.println("<li>Rating: ");
                    while (ratingResultSet.next()) {
                        float rating = ratingResultSet.getFloat("rating");
                        out.print(""+rating+ "</li>");
                    }
                    ratingResultSet.close();
                    ratingStatement.close();
                }
                else {
                    out.println("<li>Rating: " + movieRating + "</li>");
                }
                out.println("</ul>");
            }

            out.println("<h3><a class=\"active\" href=\"top20?\"> <- Back to Top 20 Best Rated Movies <h3>");
            out.println("</body>");


            resultSet.close();
            statement.close();
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

        out.println("</html>");
        out.close();

    }


}