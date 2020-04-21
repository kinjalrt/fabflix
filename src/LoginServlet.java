import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

        response.setContentType("application/json"); // Response mime type

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        JsonObject responseJsonObject = new JsonObject();


        //checking for the email and password in the database
        try{
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String query  = "SELECT * FROM customers as c WHERE c.email = \""+email+"\" AND c.password = \""+password+"\";";
            ResultSet rs = statement.executeQuery(query);

            if(rs.next()){
                //login success:

                //set this user into the session
                request.getSession().setAttribute("user", new User(rs));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else{
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                responseJsonObject.addProperty("message", "Invalid user or password");

            }
        }
        catch (Exception e) {
            // write error message JSON object to output
            responseJsonObject.addProperty("message", e.getMessage());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        response.getWriter().write(responseJsonObject.toString());



    }
}
