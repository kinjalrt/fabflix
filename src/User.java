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



    public User(int id, String firstName, String lastName, String address, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
    }

    public int getId() {
        return id;
    }
}