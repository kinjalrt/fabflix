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

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

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

          //  Connection dbcon = dataSource.getConnection();
            String param_genre = request.getParameter("genre");

            JsonArray jsonArray = new JsonArray();

            //get list of all genres for browsing
            if(param_genre!=null && param_genre.equals("set")){
                String query  = "SELECT DISTINCT * FROM genres ORDER BY name";
                PreparedStatement statement = dbcon.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    int genre_id = rs.getInt("id");
                    String genre_name = rs.getString("name");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("genre_id", genre_id);
                    jsonObject.addProperty("genre_name", genre_name);
                    jsonArray.add(jsonObject);
                }
                out.write(jsonArray.toString());
                response.setStatus(200);
                rs.close();
                statement.close();
                dbcon.close();
            }

            //check if search query empty and no browsing by char/genre requested
            else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "empty query");
                jsonArray.add(jsonObject);
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);
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
