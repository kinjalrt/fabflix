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
import java.sql.*;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        String star = request.getParameter("star");
        String birthYearString = request.getParameter("birthYear");

        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        boolean isStarAddUsed = false;

        try {
            Connection dbcon = dataSource.getConnection();

            //Add star
            if (!star.equals("null") && !birthYearString.equals("null")) {
                System.out.println(star);
                String new_id = this.findId(dbcon,"stars", "nm");
                String check_id = this.checkIfAlreadyInTable(dbcon, star,"stars");
                System.out.println(check_id);
                if(check_id.equals("")) {
                    String query = "insert into stars (id,name,birthYear) values(?,?,?)";
                    PreparedStatement statement = dbcon.prepareStatement(query);
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
                isStarAddUsed = true;

            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "");
                jsonArray.add(jsonObject);
                isStarAddUsed = true;
            }
            //end - Add star

            //display metadata
            if(!isStarAddUsed){
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
            }

            //end - display metadata
            rsTables.close();
            tableStatement.close();
            dbcon.close();

        } catch (Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "Oops! Something went wrong");
            jsonArray.add(jsonObject);

        }

        // Output stream to STDOUT
        out.write(jsonArray.toString());
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
    private String checkIfAlreadyInTable(Connection dbcon, String name, String table) throws SQLException {
        String query = "select * from "+table+" where name like ?";
        PreparedStatement check_statement = dbcon.prepareStatement(query);
        check_statement.setString(1, "%"+name+"%");
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