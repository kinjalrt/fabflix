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
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


/**
 * This CheckoutServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/checkout.
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

        //Output stream to STDOUT
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

           //prepare query and execute
            String query  = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, CCN);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setString(4, exp);

            ResultSet rs = statement.executeQuery();

            System.out.println("EXECUTED QUERY");

            //execute if customer info valid
            if(rs.next()){
                rs.beforeFirst();
                System.out.println("VALID");

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

                    JsonObject jsonObject = new JsonObject();

                    String title = (String)mapElement.getKey();
                    int quantity = ((int)mapElement.getValue());
                    System.out.println(((String)mapElement.getKey()) + " " + ((int)mapElement.getValue()));

                    //prepare query and execute
                    String query2 = "SELECT id FROM movies WHERE title = ?;";
                    PreparedStatement statement2 = dbcon.prepareStatement(query2);
                    statement2.setString(1, title);
                    ResultSet rs2 = statement2.executeQuery();

                    String movieId = "";
                    while (rs2.next()){
                        movieId = rs2.getString("id");
                    }
                    System.out.println(movieId);

                    //prepare query and execute
                    String query3 = "INSERT INTO sales (customerId,movieId,saleDate,quantity) VALUES (?, ?, ?, ?);";
                    PreparedStatement preparedStmt = dbcon.prepareStatement(query3);
                    preparedStmt.setInt (1, customerId);
                    preparedStmt.setString (2, movieId);
                    preparedStmt.setString (3, date);
                    preparedStmt.setInt (4, quantity);
                    preparedStmt.execute();


                    //retrieve each sale
                    ////prepare query and execute
                    String query4 = "SELECT id FROM sales ORDER BY id DESC LIMIT 1;";
                    PreparedStatement statement4 = dbcon.prepareStatement(query4);
                    ResultSet rs4 = statement4.executeQuery();

                    String saleID = "";
                    while (rs4.next()){
                        saleID = rs4.getString("id");
                    }
                    System.out.println(saleID);
                    jsonObject.addProperty("title", title);
                    jsonObject.addProperty("saleID", saleID);
                    jsonObject.addProperty("quantity", quantity);

                    jsonArray.add(jsonObject);

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

        //write error message JSON object to output
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorMessage", e.getMessage());
        out.write(jsonObject.toString());

        //set reponse status to 500 (Internal Server Error)
        response.setStatus(500);

    }
        out.close();


    }
}
