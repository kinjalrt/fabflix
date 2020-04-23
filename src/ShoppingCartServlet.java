import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
        String item = request.getParameter("item");
        System.out.println(item);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        HashMap<String, Integer> previousItems = (HashMap<String, Integer>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new HashMap<String, Integer>();
            previousItems.put(item, 1);
            session.setAttribute("previousItems", previousItems);
            System.out.println(previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                if (previousItems.containsKey(item)) {
                    int prev = previousItems.get(item);
                    previousItems.replace(item, prev+1);
                }
                else{
                    previousItems.put(item, 1);
                }
            }
            System.out.println(previousItems);
        }

    //    response.getWriter().write(String.join(",", previousItems));
    }

    /**
     * handles POST requests to add and show the item list information
     */
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String item = request.getParameter("item");
//        System.out.println(item);
//        HttpSession session = request.getSession();
//
//        // get the previous items in a ArrayList
//        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
//        if (previousItems == null) {
//            previousItems = new ArrayList<>();
//            previousItems.add(item);
//            session.setAttribute("previousItems", previousItems);
//        } else {
//            // prevent corrupted states through sharing under multi-threads
//            // will only be executed by one thread at a time
//            synchronized (previousItems) {
//                previousItems.add(item);
//            }
//        }
//
//        response.getWriter().write(String.join(",", previousItems));
//    }
}
