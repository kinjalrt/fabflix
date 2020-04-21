import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String email;



    public User(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        firstName = rs.getString("firstName");
        lastName = rs.getString("lastName");
        address = rs.getString("address");
        email = rs.getString("email");
    }

}