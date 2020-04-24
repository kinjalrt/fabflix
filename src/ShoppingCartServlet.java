import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/cart")
public class ShoppingCartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();
        String item = request.getParameter("item");
        String action = request.getParameter("action");
        System.out.println(item);
        System.out.println(action);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        HashMap<String, Integer> previousItems = (HashMap<String, Integer>) session.getAttribute("previousItems");

        String result = "";

        if(action!=null && action.equals("remove")){
            synchronized (previousItems) {
                    int prev = previousItems.get(item);
                    previousItems.replace(item, prev-1);
                    result = "Successfully deleted a copy of "+ item +" from shopping cart";
                    if(previousItems.get(item) == 0){
                        previousItems.remove(item);
                        result = "Successfully removed "+ item +" from shopping cart";
                    }
            }
        }
        else if(action!=null && action.equals("del")){
            synchronized (previousItems) {
                previousItems.remove(item);
            }
            result = "Successfully removed "+ item +" from shopping cart";
        }
        else if(action!=null && action.equals("add")) {
            if (previousItems == null) {
                previousItems = new HashMap<String, Integer>();
                previousItems.put(item, 1);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    if (previousItems.containsKey(item)) {
                        int prev = previousItems.get(item);
                        previousItems.replace(item, prev + 1);
                    } else {
                        previousItems.put(item, 1);
                    }
                }
            }
            result = "Successfully added a copy of "+ item +" to shopping cart";
        }
        System.out.println(previousItems);

        if(previousItems.size()>0) {
            for (Map.Entry mapElement : previousItems.entrySet()) {
                JsonObject jsonObject = new JsonObject();
                String key = (String) mapElement.getKey();
                int value = ((int) mapElement.getValue());
                jsonObject.addProperty("title", key);
                jsonObject.addProperty("quantity", value);
                jsonObject.addProperty("result", result);
                System.out.println(key + " : " + value);
                jsonArray.add(jsonObject);
            }
        }
        else{
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cart_status", "empty");
            jsonObject.addProperty("result", result);
            jsonArray.add(jsonObject);
        }
        out.write(jsonArray.toString());
        // set response status to 200 (OK)
        response.setStatus(200);

        //    response.getWriter().write(String.join(",", previousItems));
    }

}
