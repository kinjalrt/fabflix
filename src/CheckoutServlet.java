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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
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

            //get customer info from transaction form
            String first_name = request.getParameter("first_name");
            String last_name = request.getParameter("last_name");
            String CCN = request.getParameter("credit_card_number");
            String exp = request.getParameter("expiration_date");
            System.out.println(first_name+ " " + last_name+ " " + CCN +" "+ exp);

            //check if credit card info valid
            Statement statement = dbcon.createStatement();
            String query  = "SELECT * FROM creditcards WHERE id = \"" + CCN + "\" AND firstName = \"" + first_name + "\" AND " +
                    "lastName = \""+ last_name + "\" AND expiration = \"" + exp +"\"";
            ResultSet rs = statement.executeQuery(query);

            System.out.println("EXECUTED QUERY");
            //execute if customer info valid
            if(rs.next()){
                rs.beforeFirst();
                System.out.println("VALID");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("result", "valid");
                jsonArray.add(jsonObject);

                //add transaction to sales table in db
                HttpSession session = request.getSession();
                User current = (User)session.getAttribute("user");
                int customerId = current.getId();
                System.out.println(current.getId());

                HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("previousItems");

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);
                System.out.println(date);

                //each distinct movie bought by user corresponds to new row in sales table with different transactionID but same customerID
                for (Map.Entry mapElement : cart.entrySet()){
                    String title = (String)mapElement.getKey();
                    int quantity = ((int)mapElement.getValue());
                    System.out.println(((String)mapElement.getKey()) + " " + ((int)mapElement.getValue()));

                    Statement statement2 = dbcon.createStatement();
                    String query2 = "SELECT id FROM movies WHERE title = \""+ title +"\";";
                    ResultSet rs2 = statement2.executeQuery(query2);
                    String movieId = "";
                    while (rs2.next()){
                        movieId = rs2.getString("id");
                    }
                    System.out.println(movieId);

                    String query3 = "INSERT INTO sales (customerId,movieId,saleDate,quantity) VALUES (?, ?, ?, ?);";
                    PreparedStatement preparedStmt = dbcon.prepareStatement(query3);
                    preparedStmt.setInt (1, customerId);
                    preparedStmt.setString (2, movieId);
                    preparedStmt.setString (3, date);
                    preparedStmt.setInt (4, quantity);
                    preparedStmt.execute();

                    rs2.close();
                    statement2.close();

                }

                //empty shopping cart after transaction complete
                session.setAttribute("previousItems", null);

            }
            else{
                //execute if customer info invalid
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result", "invalid");
                    jsonArray.add(jsonObject);
                    System.out.println("INVALID");
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
