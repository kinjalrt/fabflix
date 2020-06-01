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
@WebServlet(name = "MovieSuggestions", urlPatterns = "/movie-suggestions")
public class MovieSuggestions extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

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

            // setup the response json arrray
           // Connection dbcon = dataSource.getConnection();
            PreparedStatement statement = null;

            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars


            String[] splited = query.trim().split("\\s+");
            String search_title = "";
            for (String i : splited){
                System.out.println(i);
                search_title+="+"+i+"* ";
            }
            System.out.println(search_title);

            String sqlQuery = "SELECT DISTINCT id, title\n" +
                    "FROM movies\n" +
                    "WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10 ";
            statement = dbcon.prepareStatement(sqlQuery);
            statement.setString(1, search_title);

            ResultSet rs = statement.executeQuery();


                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String title = rs.getString("title");
                    jsonArray.add(generateJsonObject(movie_id, title));

                }


            response.getWriter().write(jsonArray.toString());

            rs.close();
            statement.close();
            dbcon.close();
            return;

        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }

    }

    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
