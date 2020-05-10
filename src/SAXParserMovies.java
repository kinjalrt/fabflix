
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserMovies extends DefaultHandler {

    private static Connection connection = null;


    ArrayList<DirectorFilms> listDirectorFilms;
    private String director;

    private String tempVal;

    //to maintain context
    private DirectorFilms tempMovie;

    public SAXParserMovies() {
        listDirectorFilms = new ArrayList<DirectorFilms>();
    }

    public void runExample() throws Exception {
        parseDocument();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //change encoding
            File file = new File("mains243.xml");
            InputStream inputStream= new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream,"ISO-8859-1");

            InputSource is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");

            //parse the file and also register this class for call backs
            sp.parse(is, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() throws Exception {

        System.out.println("Director Name: " + director);

        // for each film:
            // check if year and genre and title all exist
            // if so get highest movie id and create new movie id
            // insert in movies tables
            // deal with genres_in_movies


        // for each film:
        for(DirectorFilms film : listDirectorFilms){

            // check if year and genre and title all exist
            if (!film.getMovieTitle().equals("NKD") && film.getMovieYear()!=0 && film.getMovieGenres().size()>0){
                System.out.println(film.toString());


                //check if movie in movies table -> if it is get movieID and go to genres directly
                String query0 = "select id from movies_test where title = ?;";
                PreparedStatement ps0 = connection.prepareStatement(query0);
                ps0.setString (1, film.getMovieTitle());
                ResultSet rs0 = ps0.executeQuery();
                String maxId = "";

                if (rs0.next()){
                    //movie already exists in table -> retrieve movie id
                    maxId = rs0.getString("id");
                    System.out.println("MOVIEID "+ maxId);

                }
                else{
                    //movie does not exist in table:
                    //get highest movie id and create new movie id
                    String maxIdQuery = "select concat(\"tt\", (substring(max(id), 3)+1)) as id from movies_test;";
                    PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
                    ResultSet rs = maxIdStatement.executeQuery();
                    while(rs.next()){
                        maxId = rs.getString("id");
                    }
                    rs.close();
                    maxIdStatement.close();

                    //add movie to movies table if not already on there
                    String query3 = "INSERT INTO movies_test (id,title,year,director)\n" +
                            "SELECT ?, ?, ?, ? FROM DUAL \n" +
                            " WHERE NOT EXISTS (SELECT * FROM movies_test       \n" +
                            " WHERE title = ? LIMIT 1);";
                    PreparedStatement preparedStmt = connection.prepareStatement(query3);
                    preparedStmt.setString (1, maxId);
                    preparedStmt.setString (2, film.getMovieTitle());
                    preparedStmt.setInt (3, film.getMovieYear());
                    preparedStmt.setString (4, director);
                    preparedStmt.setString (5, film.getMovieTitle());
                    preparedStmt.execute();
                    preparedStmt.close();

                }
                rs0.close();
                ps0.close();


                //genres
                //for each genre in that movie:
                for (String genre : film.getMovieGenres()){
                    //if genre not in genres table add
                    String query4 = "INSERT INTO genres_test (name) SELECT ? FROM DUAL WHERE NOT EXISTS (SELECT * FROM genres_test WHERE name = ? LIMIT 1);";
                    PreparedStatement ps4 = connection.prepareStatement(query4);
                    ps4.setString (1, genre);
                    ps4.setString (2, genre);
                    ps4.execute();
                    ps4.close();

                    //get genreID
                    String query5 = "select id from genres_test where name = ?;";
                    PreparedStatement ps5 = connection.prepareStatement(query5);
                    ps5.setString (1, genre);
                    ResultSet rs5 = ps5.executeQuery();
                    int genreId = 0;
                    while(rs5.next()){
                        genreId = rs5.getInt("id");
                        System.out.println("GENRE "+ genreId);
                    }
                    rs5.close();
                    ps5.close();

                    //add movieID + genreID to genres_in_movies
                    String query6 = "INSERT INTO genres_in_movies (genreId,movieId) SELECT ?, ? FROM DUAL " +
                            "WHERE NOT EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movieId = ? LIMIT 1);";
                    PreparedStatement ps6 = connection.prepareStatement(query6);
                    ps6.setInt (1, genreId);
                    ps6.setString(2, maxId);
                    ps6.setInt (3, genreId);
                    ps6.setString(4, maxId);
                    ps6.execute();
                    ps6.close();


                }

                System.out.println("ADDED");


            }

        }


        System.out.println();
    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("directorfilms")) {
            //array of all films for current director
            listDirectorFilms = new ArrayList<DirectorFilms>();
            director = "null";
        }
        else if(qName.equalsIgnoreCase("film")){
            tempMovie = new DirectorFilms();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            listDirectorFilms.add(tempMovie);

        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setMovieTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMovie.setMovieYear(Integer.parseInt(tempVal));
            }catch (Exception e){
                //specify for which movie?
                System.out.println("EXCEPTION - inconsistent data movie year: " + tempVal);
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            tempMovie.addMovieGenre(tempVal);
        }
        else if(qName.equalsIgnoreCase("dirname")){
            director = tempVal;
        }
        else if(qName.equalsIgnoreCase("directorfilms")){
            try {
                printData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";
        try {
            connection = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SAXParserMovies spe = new SAXParserMovies();
        spe.runExample();

        connection.close();

    }

}

