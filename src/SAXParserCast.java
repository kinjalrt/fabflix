
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

public class SAXParserCast extends DefaultHandler {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    private String currentMovie;
    ArrayList<String> listActors;
    private String tempVal;

    //to maintain context

    public SAXParserCast() {
        listActors = new ArrayList<String>();
    }

    public void runExample() throws Exception {
        parseDocument();
    //    printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //change encoding
            File file = new File("casts124.xml");
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

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                "mytestuser", "mypassword");
        if (connection != null) {
            System.out.println("Connection established!!");
        }

        System.out.println(currentMovie);


            //get movieId
            String query0 = "select id from movies_test where title = ?;";
            PreparedStatement ps0 = connection.prepareStatement(query0);
            ps0.setString (1, currentMovie);
            ResultSet rs0 = ps0.executeQuery();
            String movieId = "";

            //if movie in db
            if (rs0.next()){
                movieId = rs0.getString("id");
                System.out.println(currentMovie+" "+movieId);

                //for each actor
                for(String a : listActors){

                    String query1 = "select id from stars_test where name = ?;";
                    PreparedStatement ps1 = connection.prepareStatement(query1);
                    ps1.setString (1, a);
                    ResultSet rs1 = ps1.executeQuery();
                    String starId = "";

                    //if star in db
                    if(rs1.next()){
                        starId = rs1.getString("id");
                        System.out.println(a+" "+starId);

                        String query6 = "INSERT INTO stars_in_movies_test (starId,movieId) SELECT ?, ? FROM DUAL " +
                                "WHERE NOT EXISTS (SELECT * FROM stars_in_movies_test WHERE starId = ? AND movieId = ? LIMIT 1);";
                        PreparedStatement ps6 = connection.prepareStatement(query6);
                        ps6.setString (1, starId);
                        ps6.setString(2, movieId);
                        ps6.setString (3, starId);
                        ps6.setString(4, movieId);
                        ps6.execute();
                        ps6.close();

                    }
                    else{
                        System.out.println("Star \""+a+"\" does not exist in database; ignoring star-movie mapping " + a + "-" + currentMovie);

                    }
                    rs1.close();
                    ps1.close();

                }

            } else{
                System.out.println("Movie \""+currentMovie+"\" does not exist in database");

            }

        rs0.close();
        ps0.close();
        connection.close();

        System.out.println();
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //array of all films for current director
            listActors = new ArrayList<String>();
            currentMovie = "";
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("t")) {
            //add it to the list
            currentMovie = tempVal;
        }
        else if (qName.equalsIgnoreCase("a")) {
            try {
                if(tempVal.equals("sa") || tempVal.equals("s a")){
                    System.out.println("EXCEPTION \""+ tempVal +"\"- actor name not specified for movie \""+ currentMovie +"\"; ignoring this actor-movie mapping");
                }
                else{
                    listActors.add(tempVal);
                }
            }catch (Exception e){
                //specify for which movie?
                System.out.println("EXCEPTION \""+ tempVal +"\" - invalid data \""+ currentMovie+"\"");

            }
        }
        else if(qName.equalsIgnoreCase("filmc")){
            try {
                printData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    public static void main(String[] args) throws Exception {

        SAXParserCast spe = new SAXParserCast();
        spe.runExample();
    }

}

