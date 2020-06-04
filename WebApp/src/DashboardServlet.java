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
import java.sql.*;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    @Resource(name= "jdbc/moviedbMaster")
    private DataSource dataSourceMaster;

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        String star = request.getParameter("star");
        String birthYearString = request.getParameter("birthYear");
        String director = request.getParameter("director");
        String movieYearString = request.getParameter("movieYear");
        String title = request.getParameter("title");
        String movieStar = request.getParameter("movieStar");
        String movieGenre = request.getParameter("movieGenre");


        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        boolean isStatusSet = false;

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            //Master for write queries
            DataSource dsMaster = (DataSource) envContext.lookup("jdbc/moviedbMaster");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            Connection dbcon = ds.getConnection();
            Connection dbconMaster = dsMaster.getConnection();
            if (dbcon == null)
                System.out.println("dbcon is null.");
            if (dbconMaster == null)
                System.out.println("dbconMaster is null.");
         //   Connection dbcon = dataSource.getConnection();

            //Add star
            if (!star.equals("null") && !birthYearString.equals("null")) {
                System.out.println(star);
                String new_id = this.findId(dbcon,"stars", "nm");
                String check_id = this.checkIfAlreadyInTable(dbcon, star,"stars");
                System.out.println(check_id);
                if(check_id.equals("")) {
                    String query = "insert into stars (id,name,birthYear) values(?,?,?)";
                    PreparedStatement statement = dbconMaster.prepareStatement(query);
                    statement.setString(1, new_id);
                    statement.setString(2, star);
                    if(!birthYearString.equals("")) {
                        int birthYear = Integer.parseInt(birthYearString);
                        statement.setInt(3, birthYear);
                    }
                    else
                        statement.setNull(3,java.sql.Types.INTEGER);
                    statement.execute();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("status", "Successfully added "+star+" at id: "+new_id);
                    jsonArray.add(jsonObject);
                    statement.close();
                }
                else{
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("status", "Star already exists at "+check_id);
                    jsonArray.add(jsonObject);
                }
                isStatusSet = true;

            }
            //end - Add star

            //add movie procedure
            else if(isAddMovie(director,title,movieYearString,movieStar,movieGenre)) {
                int movieYear = Integer.parseInt(movieYearString);
                String checkId = checkIfMovieAlreadyInTable(dbcon,title,director,movieYear);
                if(checkId.equals("")) {
                    String movieId = findId(dbcon, "movies", "tt");

                    //check if star or genre already exists and get their id

                    String query = "call add_movie(?,?,?,?,?,?)";
                    CallableStatement add_movie_procedure = dbconMaster.prepareCall(query);
                    add_movie_procedure.setString(1, director);
                    add_movie_procedure.setString(2, movieId);
                    add_movie_procedure.setString(3, title);
                    add_movie_procedure.setInt(4, movieYear);
                    add_movie_procedure.setString(5, movieStar);
                    add_movie_procedure.setString(6, movieGenre);

                    ResultSet rs = add_movie_procedure.executeQuery();
                    String ids = "";
                    int index = 0;
                    while (rs.next()) {
                        ids += ", genre id: " + rs.getInt("@gid");
                        ids += ", star id: " + rs.getString("@sid");
                    }

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("status", "Movie Added Successfully at movie id: "+ movieId + ids);
                    jsonArray.add(jsonObject);

                }
                else{
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("status", "Movie already exists at "+checkId);
                    jsonArray.add(jsonObject);
                }
                isStatusSet = true;
            }
            else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "");
                jsonArray.add(jsonObject);
                isStatusSet = true;
            }
            //end - add movie procedure

            //display metadata
            if(!isStatusSet){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "");
                jsonArray.add(jsonObject);
            }
            PreparedStatement tableStatement = dbcon.prepareStatement("show tables");
            ResultSet rsTables = tableStatement.executeQuery();
            while (rsTables.next()) {
                JsonObject jsonObjectTables = new JsonObject();
                String tableName = rsTables.getString("Tables_in_moviedb");
                jsonObjectTables.addProperty("table_name", tableName);
                PreparedStatement queryStatement = dbcon.prepareStatement("describe "+tableName);
                ResultSet resultSet = queryStatement.executeQuery();

                int count = 0;
                while(resultSet.next()){
                    String nullable = "";
                    String key = "";
                    if(resultSet.getString("Null").equals("NO"))
                        nullable = "NOT NULL";
                    if(!resultSet.getString("Key").equals(""))
                        key = "(PRIMARY KEY)";
                    String name = String.format("%s %s",resultSet.getString("Field"), key);
                    String type = String.format("%s %s",resultSet.getString("Type"),nullable);

                    jsonObjectTables.addProperty("attr_name"+(++count),name);
                    jsonObjectTables.addProperty("attr_type"+count,type);
                }
                jsonObjectTables.addProperty("col_count",count);

                jsonArray.add(jsonObjectTables);
                resultSet.close();
                queryStatement.close();
            }
            //end - display metadata

            rsTables.close();
            tableStatement.close();
            dbcon.close();
            dbconMaster.close();

        } catch (Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "Oops! Something went wrong: "+e.getMessage());
            jsonArray.add(jsonObject);

        }

        // Output stream to STDOUT
        out.write(jsonArray.toString());
    }

    private boolean isAddMovie(String director, String title, String movieYearString, String movieStar, String movieGenre){
        return !director.equals("null") && !title.equals("null") && !movieYearString.equals("null")
            && !movieGenre.equals("null") && !movieStar.equals("null");
    }

    private String findId(Connection dbcon, String table, String pref) throws SQLException {
        String starId_query = "select max(id) from "+table;
        PreparedStatement starId_statement = dbcon.prepareStatement(starId_query);
        ResultSet find_resultSet = starId_statement.executeQuery();
        String id = "";
        while (find_resultSet.next()){
            id = pref + (Integer.parseInt(find_resultSet.getString("max(id)").substring(2))+1);
        }
        find_resultSet.close();
        starId_statement.close();
        return id;
    }

    private String checkIfMovieAlreadyInTable(Connection dbcon, String title, String director, int year) throws SQLException{
        String query = "select * from movies where upper(title) like upper(?) and upper(director) like upper(?) and year = ?";
        PreparedStatement check_statement = dbcon.prepareStatement(query);
        check_statement.setString(1, title);
        check_statement.setString(2, director);
        check_statement.setInt(3, year);
        ResultSet check_resultSet = check_statement.executeQuery();
        if (check_resultSet.next()) {
            String id = check_resultSet.getString("id");
            check_resultSet.close();
            check_statement.close();
            return id;
        }
        else {
            check_resultSet.close();
            check_statement.close();
            return "";
        }
    }

    private String checkIfAlreadyInTable(Connection dbcon, String name, String table) throws SQLException {
        String query = "select * from " +table+ " where upper(name) = upper(?)";
        PreparedStatement check_statement = dbcon.prepareStatement(query);
        check_statement.setString(1, name);
        ResultSet check_resultSet = check_statement.executeQuery();
        if (check_resultSet.next()) {
            String id = check_resultSet.getString("id");
            check_resultSet.close();
            check_statement.close();
            return id;
        }
        else {
            check_resultSet.close();
            check_statement.close();
            return "";
        }
    }
}