import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();

            String first_name = request.getParameter("first_name");
            String last_name = request.getParameter("last_name");
            String CCN = request.getParameter("credit_card_number");
            String exp = request.getParameter("expiration_date");
            System.out.println(first_name+ " " + last_name+ " " + CCN +" "+ exp);

            Statement statement = dbcon.createStatement();
            String query  = "SELECT * FROM creditcards WHERE id = \"" + CCN + "\" AND firstName = \"" + first_name + "\" AND " +
                    "lastName = \""+ last_name + "\" AND expiration = \"" + exp +"\"";
            ResultSet rs = statement.executeQuery(query);

            if(!rs.isBeforeFirst()){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "invalid information");
                jsonArray.add(jsonObject);
                System.out.println("INVALID");
            }
            else{
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "valid information");
                jsonArray.add(jsonObject);
                System.out.println("BOKE HINATA BOKE");
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();
            dbcon.close();

        }
         catch (Exception e) {

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
