import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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

            // Get a connection from dataSource
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

         //   Connection dbcon = dataSource.getConnection();

            JsonArray jsonArray = new JsonArray();

            String query  = "";
            PreparedStatement statement = null;
            int param_index = 0;
            String sort_string = sort(param_sort);
            String num_string = num(param_num);
            String first_record = firstRecord(param_first_record);

            //search by letters
            if(param_char != null && !param_char.equals("null") && !param_char.isEmpty()){
                param_index = 0;
                String char_string = charString(param_char);
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM movies as m, ratings as r\n" +
                        "WHERE m.id = r.movieId AND m.title" + char_string +
                        sort_string + num_string + first_record;
                statement = dbcon.prepareStatement(query);
                if(!param_char.equals("*")){
                    statement.setString(++param_index, param_char+"%");
                }
            }
            //search by genre
            else if(param_gid != null && !param_gid.equals("null") && !param_gid.isEmpty()){
                param_index = 0;
                System.out.println(sort_string);
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM genres_in_movies as gim, movies as m LEFT JOIN ratings as r ON m.id = r.movieId\n" +
                        "WHERE gim.genreId = ? AND m.id = gim.movieId \n"+
                        sort_string + num_string + first_record;
                statement = dbcon.prepareStatement(query);
                statement.setString(++param_index, param_gid);
            }
            //if title is NOT empty AND the ONLY param -> FOR MAIN SEARCH mostly
            else if(!param_title.equals("null") && param_year.equals("null") && param_dir.equals("null") && param_star.equals("null") ){
                //splitting title
                System.out.println("atsumu");
                String[] splited = param_title.trim().split("\\s+");
                String search_title = "";
                for (String i : splited){
                    System.out.println(i);
                    search_title+="+"+i+"* ";
                }
                System.out.println(search_title);

                String sqlQuery = "SELECT DISTINCT id, title, year, director, rating\n" +
                        "FROM movies as m LEFT JOIN ratings as r ON m.id = r.movieId\n" +
                        "WHERE MATCH (m.title) AGAINST (? IN BOOLEAN MODE) " +
                        sort_string + num_string + first_record;
                statement = dbcon.prepareStatement(sqlQuery);
                statement.setString(1, search_title);System.out.println("osamu 321");


            }
            //title NOT empty + other params; match against does not work w empty param
            else if(param_title != null && !param_title.equals("null") && !param_title.isEmpty()){
                //splitting title
                String[] splited = param_title.trim().split("\\s+");
                String search_title = "";
                for (String i : splited){
                    System.out.println(i);
                    search_title+="+"+i+"* ";
                }
                System.out.println(search_title);

                param_index = 0;
                String add_year = year(param_year);
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM stars_in_movies as sim, stars as s, movies as m LEFT JOIN ratings as r ON m.id = r.movieId\n" +
                        "WHERE MATCH (m.title) AGAINST (? IN BOOLEAN MODE) "+add_year+"AND m.director LIKE ?\n"+
                        "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE ?" +
                        sort_string + num_string + first_record;
                statement = dbcon.prepareStatement(query);
                statement.setString(++param_index, search_title);
                if(!add_year.equals(" ")){
                    statement.setInt(++param_index, Integer.parseInt(param_year));
                }
                statement.setString(++param_index, "%"+param_dir+"%");
                statement.setString(++param_index, "%"+param_star+"%");

            }
            else
            {
                System.out.println("nagisa "+ param_sort + " "+ num_string + " "+ first_record);
                param_index = 0;
                String add_year = year(param_year);
                query = "SELECT DISTINCT m.id, title, year, director, rating\n" +
                        "FROM stars_in_movies as sim, stars as s, movies as m LEFT JOIN ratings as r ON m.id = r.movieId\n" +
                        "WHERE m.title LIKE ? "+add_year+"AND m.director LIKE ?\n"+
                        "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE ?" +
                        sort_string + num_string + first_record;
                statement = dbcon.prepareStatement(query);
                statement.setString(++param_index, "%"+param_title+"%");
                if(!add_year.equals(" ")){
                    statement.setInt(++param_index, Integer.parseInt(param_year));
                }
                statement.setString(++param_index, "%"+param_dir+"%");
                statement.setString(++param_index, "%"+param_star+"%");
            }

            if(!num_string.equals(" LIMIT 20\n")){
                statement.setInt(++param_index, Integer.parseInt(param_num));
            }
            if(!first_record.equals(" ")){
                statement.setInt(++param_index, Integer.parseInt(param_first_record));
            }

            // Perform the query
            ResultSet rs = statement.executeQuery();

            //check if results of list of movies > 0
            if (!rs.isBeforeFirst()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "No results found for this query");
                jsonArray.add(jsonObject);
            }
            else {
                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String title = rs.getString("title");
                    int year = rs.getInt("year");
                    String dir = rs.getString("director");

                    String q0 = "SELECT rating\n" +
                            "FROM movies as m, ratings as r \n" +
                            "WHERE  m.id = ? AND m.id = r.movieId; ";
                    PreparedStatement s0 = dbcon.prepareStatement(q0);
                    s0.setString(1, movie_id);
                    ResultSet r0 = s0.executeQuery();
                    float rating = 0;
                    if (r0.next()) {
                        float g = r0.getFloat("rating");
                        rating = g;
                    }
                    r0.close();
                    s0.close();

                    //float rating = rs.getFloat("rating");

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
                            "WHERE  m.title = ? AND m.id = sim.movieId AND sim.starId = s.id) as f\n" +
                            "NATURAL JOIN stars_in_movies\n" +
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

    private String firstRecord(String param_first_record){
        if(param_first_record.equals("null") || param_first_record.isEmpty()){
            return " ";
        }
        else
            return " OFFSET ? ";
    }

    private String year(String param_year){
        if(param_year.equals("null") || param_year.isEmpty())
            return " ";
        else
            return " AND m.year = ? ";
    }

    private String num(String param_num){
        if(param_num.equals("null") || param_num.equals("")){
            return " LIMIT 20\n";
        } else {
            return " LIMIT ?\n";
        }
    }

    private String charString(String param_char){
        if(param_char.equals("*")){
            return " REGEXP '^[^0-9A-Za-z]' ";
        } else {
            return " LIKE ? ";
        }
    }

    private String sort(String param_sort){
        if(param_sort.equals("null") || param_sort.equals("")){
            return " ";
        } else {
            return " ORDER BY "+param_sort+"\n";
        }
    }
}
