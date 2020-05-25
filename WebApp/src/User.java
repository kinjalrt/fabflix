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
    private final String type;
    private final String fullName;



    public User(int id, String firstName, String lastName, String address, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.address = address;
        this.email = email;
        this.type = "customer";

    }

    public User(String name, String email){
        this.id = -1;
        this.fullName = name;
        this.firstName = name.split(" ")[0];
        this.email = email;
        this.lastName = name.split(" ")[1];
        this.address = "";
        this.type = "employee";
    }

    public int getId() {
        return id;
    }

    public String getType() {return type;}
}