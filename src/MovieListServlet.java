import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/top20"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/top20")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            //get parameters from url
            String param_title = request.getParameter("title");
            String param_year = request.getParameter("year");
            String param_dir = request.getParameter("director");
            String param_star = request.getParameter("star");
            String param_gid = request.getParameter("gid");
            String param_char = request.getParameter("char");
            String param_sort = request.getParameter("sort");
            String param_num = request.getParameter("num");
            String param_first_record = request.getParameter("firstRecord");



            if(param_sort.equals("null") || param_sort.equals("")){
                param_sort = "";
            } else {
                param_sort = "ORDER BY " + param_sort;
            }
            if(param_num.equals("null") || param_num.equals("")){
                param_num = "LIMIT 20";
            } else {
                param_num = "LIMIT " + param_num;
            }
            if(param_first_record.equals("null") || param_first_record.equals("") || Integer.parseInt(param_first_record) < 0){
                param_first_record = "";
            } else {
                param_first_record = "OFFSET " + param_first_record;
            }

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            JsonArray jsonArray = new JsonArray();

            String query  = "";

            //search by letters
            if(param_char != null && !param_char.equals("null") && !param_char.isEmpty()){
                if(param_char.equals("*")){
                    param_char = "REGEXP '^[^0-9A-Za-z]'";
                }
                else{
                    param_char = "LIKE \""+param_char+"%\"";
                }
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM movies as m, ratings as r\n" +
                        "WHERE m.id = r.movieId AND m.title " + param_char + " \n"+
                        param_sort + "\n"+param_num +"\n"+param_first_record;

            }
            //search by genre
            else if(param_gid != null && !param_gid.equals("null") && !param_gid.isEmpty()){
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM movies as m, ratings as r, genres_in_movies as gim\n" +
                        "WHERE m.id = r.movieId AND gim.genreId = \"" + param_gid + "\" AND m.id = gim.movieId \n"+
                        param_sort + "\n"+param_num +"\n"+param_first_record;
            }
            else {
                if (!param_year.equals("")){
                    param_year = " AND m.year =\""+ param_year + "\"";
                }
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM movies as m, ratings as r, stars_in_movies as sim, stars as s\n" +
                        "WHERE m.title LIKE \"%" + param_title + "%\" AND m.id = r.movieId" + param_year + " AND m.director LIKE \"%" + param_dir + "%\" " +
                        "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE \"%" + param_star + "%\" " +
                        param_sort + "\n"+param_num +"\n"+param_first_record;
            }

            // Perform the query
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            //check if results of list of movies > 0
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


                    //output at most 3 genres
                    String q2 = "SELECT title, name, g.id as id\n" +
                            "FROM movies as m, genres as g, genres_in_movies as gim\n" +
                            "WHERE  m.title = ? AND m.id = gim.movieId AND gim.genreId = g.id\n" +
                            "ORDER BY name LIMIT 3";
                    PreparedStatement s2 = dbcon.prepareStatement(q2);
                    s2.setString(1, title);
                    ResultSet r2 = s2.executeQuery();
                    String genre = "genre";
                    int incr = 1;
                    while (r2.next()) {
                        String g = r2.getString("name");
                        int gid = r2.getInt("id");
                        jsonObject.addProperty(genre + incr, g);
                        jsonObject.addProperty("gid" + incr , gid); //y - added genre id
                        incr += 1;
                    }
                    r2.close();
                    s2.close();

                    //output all stars for each film
                    String q3 = "SELECT title, f.starId, f.name, count(movieId)\n" +
                            "FROM (SELECT title, s.id as starId, name FROM movies as m, stars_in_movies as sim, stars as s\n" +
                            "\tWHERE  m.title = ? AND m.id = sim.movieId AND sim.starId = s.id) as f\n" +
                            "    NATURAL JOIN stars_in_movies\n" +
                            "group by f.starId\n" +
                            "order by count(movieId) desc, f.name\n" +
                            "limit 3;";
                    PreparedStatement s3 = dbcon.prepareStatement(q3);
                    s3.setString(1, title);
                    ResultSet r3 = s3.executeQuery();
                    String starname = "starname";
                    String starid = "starid";
                    int count = 1;
                    while (r3.next()) {
                        String sname = r3.getString("name");
                        String sid = r3.getString("starId");
                        jsonObject.addProperty(starname + count, sname);
                        jsonObject.addProperty(starid + count, sid);
                        count += 1;
                    }
                    r3.close();
                    s3.close();

                    //add each movie array
                    jsonArray.add(jsonObject);
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
