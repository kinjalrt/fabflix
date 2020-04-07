import java.sql.*;

public class MovieList implements Parameters {

    public static void main(String[] arg) throws Exception {

        // Incorporate mySQL driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Connect to the test database
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();
            }

            // Create an execute an SQL statement to select all of table"Stars" records
            Statement select = connection.createStatement();
            String q1 = "SELECT DISTINCT title, year, director, rating\n" +
                    "FROM movies as m, ratings as r\n" +
                    "WHERE m.id = r.movieId\n" +
                    "ORDER BY rating DESC\n" +
                    "LIMIT 20";
            ResultSet r1 = select.executeQuery(q1);

            // Get metatdata from stars; print # of attributes in table
            System.out.println("The results of the query");
            ResultSetMetaData r1_metadata = r1.getMetaData();
            System.out.println("There are " + r1_metadata.getColumnCount() + " columns");

            // Print type of each attribute
            for (int i = 1; i <= r1_metadata.getColumnCount(); i++)
                System.out.println("Type of column " + i + " is " + r1_metadata.getColumnTypeName(i));

            // print table's contents, field by field


            while (r1.next()) {
                //   System.out.println("Id = " + result.getInt("id"));
                System.out.println("Title = " + r1.getString("title"));
                System.out.println("Year = " + r1.getInt("year"));
                System.out.println("Director = " + r1.getString("director"));
                System.out.println("Rating = " + r1.getFloat("rating"));


                Statement s2 = connection.createStatement();
                String q2 ="SELECT title, name\n" +
                        "FROM movies as m, genres as g, genres_in_movies as gim\n" +
                        "WHERE  m.title = \""+r1.getString("title") +"\" AND m.id = gim.movieId AND gim.genreId = g.id\n" +
                        "LIMIT 3";
                ResultSet r2 = s2.executeQuery(q2);
                while(r2.next()) {
                    System.out.println("Genre = " + r2.getString("name"));
                }

                Statement s3 = connection.createStatement();
                String q3 ="SELECT title, name\n" +
                        "FROM movies as m, stars_in_movies as sim, stars as s\n" +
                        "WHERE  m.title = \""+r1.getString("title")+"\" AND m.id = sim.movieId AND sim.starId = s.id\n" +
                        "LIMIT 3";
            ResultSet r3 = s3.executeQuery(q3);
            while(r3.next()) {
                System.out.println("Star = " + r3.getString("name"));
            }



            System.out.println();
        }
    }
}
