
import java.io.*;
import java.sql.*;
import java.util.*;

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

    private static Connection connection = null;

    private String currentStar;
    private int currentDob;
    private String tempVal;
    Pair<String, Integer> tempPair;

    ArrayList<Pair<String, Integer>> listActors;
    //to maintain context

    public SAXParserStars() {
        listActors = new ArrayList<Pair<String, Integer>>();
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData(ArrayList<Pair<String, Integer>> batch) throws Exception {


        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord="INSERT INTO stars (id,name,birthYear) VALUES(?, ?, ?)";

        //get max id in db
        String biggestId = "";
        String maxIdQuery = "select substring(max(id), 3) as id from stars;";
        PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
        ResultSet rs = maxIdStatement.executeQuery();
        while (rs.next()) {
            biggestId = rs.getString("id");
        }
        int maxId = (Integer.parseInt(biggestId));
        rs.close();
        maxIdStatement.close();


        try {

            connection.setAutoCommit(false);
            psInsertRecord=connection.prepareStatement(sqlInsertRecord);

            //for each actor (100)
            for (Pair<String, Integer> actor : batch) {

                String name = actor.getL();
                int dob = actor.getR();

                //create new star id
                maxId = maxId + 1;
                String starId = "nm"+(maxId);

                //add star to db
                psInsertRecord.setString(1, starId);
                psInsertRecord.setString(2, name);
                if(dob!=0) {
                    psInsertRecord.setInt(3, dob);
                  //  System.out.println("Inserting " + starId + " " + name + " " + dob);
                }else{
                    psInsertRecord.setString(3, null);
                 //   System.out.println("Inserting (dob null) " + starId + " " + name + " " + dob);
                }
                psInsertRecord.addBatch();


            }

            psInsertRecord.executeBatch();
            connection.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

      //  System.out.println();
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create new list every 100 actor
            if(listActors.size()==100) {
             //   System.out.println("reset");

                try {
                    printData(listActors);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                listActors = new ArrayList<Pair<String, Integer>>();
            }

            currentStar = "";
            currentDob = 0;
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        try(FileWriter fw = new FileWriter("ParserStars.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {

            if (qName.equalsIgnoreCase("stagename")) {
                //add it to the list
                currentStar = tempVal;
                tempPair = new Pair("", 0);

                //check if movie already in db
                try {
                    String query0 = "select * from stars where name = ?;";
                    PreparedStatement ps0 = connection.prepareStatement(query0);
                    ps0.setString(1, currentStar);
                    ResultSet rs0 = ps0.executeQuery();

                    if (rs0.next()) {
                        out.println("Star \""+currentStar+"\" already in database");
                        // System.out.println(currentStar + " already in db");
                    } else {
                        tempPair.setL(currentStar);
                    }
                    rs0.close();
                    ps0.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else if (qName.equalsIgnoreCase("dob")) {
                try {
                    currentDob = (Integer.parseInt(tempVal));
                    //update pair only if movie not in db else ignore
                    if (!tempPair.getL().equals("")) {
                        tempPair.setR(currentDob);
                    }
                } catch (Exception e) {
                    if (tempVal.equals("")) {
                        out.println("Warning -> date of birth unknown for star \""+currentStar+"\" -> setting dob as null");
                        //   System.out.println("EXCEPTION \""+ tempVal +"\"- date of birth unknown for star \""+ currentStar +"\"; setting dob as null");
                    } else {
                        out.println("Warning -> invalid date of birth \""+ tempVal +"\" for star \""+currentStar+"\" -> setting dob as null");
                        //   System.out.println("EXCEPTION \""+ tempVal +"\" - invalid date of birth data for star \""+ currentStar +"\"; setting dob as null");
                    }
                }

            } else if (qName.equalsIgnoreCase("actor")) {
                if (!tempPair.getL().equals("")) {
                    listActors.add(tempPair);
                    //  System.out.println(tempPair.getL() + " " + tempPair.getR() + " " + listActors.size());
                }

            } else if (qName.equalsIgnoreCase("actors")) {
                // System.out.println("no");
                try {
                    printData(listActors);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        try {
            File file = new File("ParserStars.txt");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "mypassword");
            if (connection != null) {
                System.out.println("Connection established!!");
            }

            SAXParserStars spe = new SAXParserStars();
            spe.runExample();
            connection.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

