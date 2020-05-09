
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

public class SAXParserStars extends DefaultHandler {


    private String currentStar;
    private int currentDob;
    private String tempVal;

    //to maintain context

    public SAXParserStars() {
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
            File file = new File("actors63.xml");
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


        String query0 = "select * from stars_test where name = ?;";
        PreparedStatement ps0 = connection.prepareStatement(query0);
        ps0.setString (1, currentStar);
        ResultSet rs0 = ps0.executeQuery();

        if (rs0.next()){
            System.out.println(currentStar + " already in db");

        }
        else{
            //create id
            String starId = "";
            String maxIdQuery = "select concat(\"nm\", (substring(max(id), 3)+1)) as id from stars_test;";
            PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
            ResultSet rs = maxIdStatement.executeQuery();
            while(rs.next()){
                starId = rs.getString("id");
            }
            rs.close();
            maxIdStatement.close();

            if(currentDob!=0){
                String query3 = "INSERT INTO stars_test (id,name,birthYear)\n" +
                        "VALUES(?, ?, ?)";
                PreparedStatement preparedStmt = connection.prepareStatement(query3);
                preparedStmt.setString (1, starId);
                preparedStmt.setString (2, currentStar);
                preparedStmt.setInt (3, currentDob);
                preparedStmt.execute();
                preparedStmt.close();

            }
            else{
                String query4 = "INSERT INTO stars_test (id,name)\n" +
                        "VALUES(?, ?)";
                PreparedStatement preparedStmt4 = connection.prepareStatement(query4);
                preparedStmt4.setString (1, starId);
                preparedStmt4.setString (2, currentStar);
                preparedStmt4.execute();
                preparedStmt4.close();

            }
            System.out.println("added");

        }
        rs0.close();
        ps0.close();
        connection.close();


        //if star not in table create id and add

        System.out.println("Star: "+currentStar);
        System.out.println("DOB: "+currentDob);
        System.out.println();
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //array of all films for current director
            currentStar = "";
            currentDob = 0;
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("stagename")) {
            //add it to the list
            currentStar = tempVal;

        }
        else if (qName.equalsIgnoreCase("dob")) {
            try {
                currentDob = (Integer.parseInt(tempVal));
            }catch (Exception e){
                if(tempVal.equals("*")){
                    System.out.println("EXCEPTION \""+ tempVal +"\"- date of birth unknown for star \""+ currentStar +"\"; setting dob as null");
                }
                else if(tempVal.equals("")){
                    System.out.println("EXCEPTION \""+ tempVal +"\"- date of birth unknown for star \""+ currentStar +"\"; setting dob as null");
                }
                else{
                    System.out.println("EXCEPTION \""+ tempVal +"\" - invalid date of birth data for star \""+ currentStar +"\"; setting dob as null");
                }
            }
        }
        else if(qName.equalsIgnoreCase("actor")){
            try {
                //put count at 100 actors -> send batch (list: <name, dob>)
                printData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    public static void main(String[] args) throws Exception {

        SAXParserStars spe = new SAXParserStars();
        spe.runExample();
    }

}

