import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                System.out.println("dbcon is null.");


            // Get a connection from dataSource
          //  Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "SELECT id, title, year, director FROM moviedb.movies as m where m.id = ?;";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                int movieYear = rs.getInt("year");
                String movieDirector = rs.getString("director");


                String q0 = "SELECT rating\n" +
                        "FROM movies as m, ratings as r \n" +
                        "WHERE  m.id = ? AND m.id = r.movieId; ";
                PreparedStatement s0 = dbcon.prepareStatement(q0);
                s0.setString(1, movieId);
                ResultSet r0 = s0.executeQuery();
                float movieRating = 0;
                if (r0.next()) {
                    float g = r0.getFloat("rating");
                    movieRating = g;
                }
                r0.close();
                s0.close();

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();


                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", movieRating);

                //adding genre
                String genreQuery = "SELECT g.name, g.id \n" +
                        "FROM moviedb.movies as m, moviedb.genres as g, moviedb.genres_in_movies as gm \n" +
                        "where title = ? and m.id = gm.movieId and gm.genreId = g.id ORDER BY name;";
                PreparedStatement genreStatement = dbcon.prepareStatement(genreQuery);
                genreStatement.setString(1, movieTitle);
                ResultSet rsg = genreStatement.executeQuery();

                int genreCount = 0;
                while(rsg.next()){
                    String g = rsg.getString("name");
                    int gid = rsg.getInt("id");
                    jsonObject.addProperty("movie_genre"+(++genreCount), g);
                    jsonObject.addProperty("movie_genre_id"+genreCount, gid);
                }
                jsonObject.addProperty("genre_count", genreCount);

                //adding stars
                String starsQuery = "SELECT title, f.starId, f.name, count(movieId)\n" +
                        "FROM (SELECT title, s.id as starId, name FROM movies as m, stars_in_movies as sim, stars as s\n" +
                        "\tWHERE  m.title = ? AND m.id = sim.movieId AND sim.starId = s.id) as f\n" +
                        "NATURAL JOIN stars_in_movies\n group by f.starId\n" +
                        "order by count(movieId) desc, f.name;";
                PreparedStatement starStatement = dbcon.prepareStatement(starsQuery);
                starStatement.setString(1, movieTitle);
                ResultSet rss = starStatement.executeQuery();

                int starCount = 0;
                while(rss.next()){
                    String s = rss.getString("name");
                    String sid = rss.getString("starId");
                    jsonObject.addProperty("movie_stars"+(++starCount), s);
                    jsonObject.addProperty("movie_stars_id"+starCount, sid);
                }
                jsonObject.addProperty("stars_count", starCount);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }

}
