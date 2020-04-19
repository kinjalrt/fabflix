import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.deploy.security.SelectableSecurityManager;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/top20")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            //get parameters from url
            String param_title = request.getParameter("title");
            String param_year = request.getParameter("year");
            String param_dir = request.getParameter("director");
            String param_star = request.getParameter("star");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();


            JsonArray jsonArray = new JsonArray();

            //check if query empty
            if( (param_dir.equals("")) && (param_star.equals("")) && (param_title.equals("")) && (param_year.equals(""))){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "empty query");
                jsonArray.add(jsonObject);
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);
            }

            else {

                Statement statement = dbcon.createStatement();
                String query  = "";

                if (param_year.equals("")) {
                    query += "SELECT DISTINCT m.id, title, year, director, rating\n" +
                            "FROM movies as m, ratings as r, stars_in_movies as sim, stars as s\n" +
                            "WHERE m.title LIKE \"%" + param_title + "%\" AND m.id = r.movieId AND m.director LIKE \"%" + param_dir + "%\" " +
                            "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE \"%" + param_star + "%\" \n" +
                            "ORDER BY rating DESC\n" +
                            "LIMIT 20 \n";
                } else {
                    query += "SELECT DISTINCT m.id, title, year, director, rating\n" +
                            "FROM movies as m, ratings as r, stars_in_movies as sim, stars as s\n" +
                            "WHERE m.title LIKE \"%" + param_title + "%\" AND m.id = r.movieId AND m.year = \"" + param_year + "\" AND m.director LIKE \"%" + param_dir + "%\" " +
                            "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE \"%" + param_star + "%\" " +
                            "ORDER BY  rating DESC\n" +
                            "LIMIT 20 \n";
                    ;
                }


                // Perform the query
                ResultSet rs = statement.executeQuery(query);

                //check if results > 0
                if (!rs.isBeforeFirst()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result", "no results found for this query");
                    jsonArray.add(jsonObject);

                }
                else {
                    // Iterate through each row of rs
                    while (rs.next()) {

                        String movie_id = rs.getString("id");
                        String title = rs.getString("title");
                        int year = rs.getInt("year");
                        String dir = rs.getString("director");
                        float rating = rs.getFloat("rating");

                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("result", "success");
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("title", title);
                        jsonObject.addProperty("year", year);
                        jsonObject.addProperty("dir", dir);
                        jsonObject.addProperty("rating", rating);

                        //output at most genres
                        Statement s2 = dbcon.createStatement();
                        String q2 = "SELECT title, name\n" +
                                "FROM movies as m, genres as g, genres_in_movies as gim\n" +
                                "WHERE  m.title = \"" + title + "\" AND m.id = gim.movieId AND gim.genreId = g.id\n" +
                                "LIMIT 3";
                        ResultSet r2 = s2.executeQuery(q2);
                        String genre = "genre";
                        int incr = 1;
                        while (r2.next()) {
                            String g = r2.getString("name");
                            jsonObject.addProperty(genre + incr, g);
                            incr += 1;
                        }
                        r2.close();
                        s2.close();

                        //output all stars for each film
                        Statement s3 = dbcon.createStatement();
                        String q3 = "SELECT title, s.id as sid, name\n" +
                                "FROM movies as m, stars_in_movies as sim, stars as s\n" +
                                "WHERE  m.title = \"" + title + "\" AND m.id = sim.movieId AND sim.starId = s.id";
                        ResultSet r3 = s3.executeQuery(q3);
                        String starname = "starname";
                        String starid = "starid";
                        int count = 1;
                        //if (!r3.isBeforeFirst() == false )
                        while (r3.next()) {
                            String sname = r3.getString("name");
                            String sid = r3.getString("sid");
                            jsonObject.addProperty(starname + count, sname);
                            jsonObject.addProperty(starid + count, sid);
                            count += 1;
                        }
                        r3.close();
                        s3.close();

                        //add each movie array
                        jsonArray.add(jsonObject);
                    }
                }

                    // write JSON string to output
                    out.write(jsonArray.toString());
                    // set response status to 200 (OK)
                    response.setStatus(200);

                rs.close();
                statement.close();
                dbcon.close();
            }
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
