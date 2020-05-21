import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        JsonObject responseJsonObject = new JsonObject();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        //recaptcha verification
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        }
        catch (Exception e){
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Recaptcha verification error");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        //checking for the email and password in the database
        try{
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT * from customers where email=?";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            boolean success = false;
            if(rs.next()){

                //email exists
                String encryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                System.out.println(success);

                //set this user into the session
                if(success) {

                    //both email and password correct
                    int id = rs.getInt("id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String address = rs.getString("address");

                    request.getSession().setAttribute("user", new User(id, firstName, lastName, address, email));


                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else{

                    //email correct but password incorrect
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Invalid password");
                }
            } else{

                // email does not exist
                // Login fail
                System.out.println("email does not exist");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid email");

            }

            rs.close();
            statement.close();
            dbcon.close();
        }
        catch (Exception e) {

            // write error message JSON object to output
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        response.getWriter().write(responseJsonObject.toString());

    }
}

