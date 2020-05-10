
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

    private static Connection connection = null;
    private String currentMovie;
    private Pair<String, String> currentPair;
    private String currentMovieId = "";
    ArrayList<Pair<String, String>> listRelations;
    private String tempVal;

    //to maintain context

    public SAXParserCast() {
        listRelations = new ArrayList<Pair<String, String>>();
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
    private void printData(ArrayList<Pair<String, String>> batch) throws Exception {

        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord="INSERT INTO stars_in_movies_test (starId,movieId) VALUES(?, ?)";


        try {

            connection.setAutoCommit(false);
            psInsertRecord = connection.prepareStatement(sqlInsertRecord);

            for (Pair<String, String> p : batch) {
                String mId = p.getL();
                String sId = p.getR();

                psInsertRecord.setString(1, sId);
                psInsertRecord.setString(2, mId);
                psInsertRecord.addBatch();
                //System.out.println("there: "+mId +" "+sId);

            }
            psInsertRecord.executeBatch();
            connection.commit();

        } catch (SQLException e) {
           // System.out.println("Movie-star relation already exists in db -> "+ e.getMessage());
           // e.printStackTrace();
          //  System.out.println(e.getMessage());
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

       // System.out.println();
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //array of all films for current director
           // System.out.println("size: "+listRelations.size());
            if(listRelations.size()>100) {
              //  System.out.println("nagisaaa");
                try {
                    printData(listRelations);
                } catch (Exception e) {
                  //  e.printStackTrace();
                }
                listRelations = new ArrayList<Pair<String, String>>();
            }
            currentMovie = "";
            currentMovieId ="";
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("t")) {
            //add it to the list
            //use pairs (movieid, starid)
            //if this is a new movie check if in db + retrieve id if it is -> once each
            if(currentMovie.equals("")){
                try{
                    currentMovie = tempVal;
                    String query0 = "select id from movies_test where title = ?;";
                    PreparedStatement ps0 = connection.prepareStatement(query0);
                    ps0.setString (1, currentMovie);
                    ResultSet rs0 = ps0.executeQuery();
                    if(rs0.next()){
                        currentMovieId = rs0.getString("id");
                        currentPair = new Pair(currentMovieId, "");

                    }
                    else{
                     //   System.out.println("Movie "+currentMovie+" does not exist in database -> ignoring all movie-star mapping related to this movie");
                    }
                    rs0.close();
                    ps0.close();

                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }
        else if (qName.equalsIgnoreCase("a")) {

            if(!currentPair.getL().equals("")) {
                String currentStar = tempVal;
                try {
                    if (currentStar.equals("sa") || currentStar.equals("s a") || currentStar.equals("") || currentStar.equals(" ")) {
                     //   System.out.println("EXCEPTION \"" + currentStar + "\"- actor name not specified for movie \"" + currentMovie + "\"; ignoring this actor-movie mapping");
                    }
                    else {
                        //check if actor in db;
                        try {

                            String query1 = "select id from stars_test where name = ?;";
                            PreparedStatement ps1 = connection.prepareStatement(query1);
                            ps1.setString (1, currentStar);
                            ResultSet rs1 = ps1.executeQuery();
                            String starId = "";
                            if(rs1.next()){
                                starId = rs1.getString("id");
                                currentPair.setR(starId);
                                listRelations.add(currentPair);
                          //      System.out.println("here: "+currentPair.getL()+ " "+currentPair.getR());
                                currentPair = new Pair(currentMovieId, "");

                            }
                            else{
                       //         System.out.println("Star \""+currentStar+"\" does not exist in database; ignoring movie-star mapping " + currentMovie + "-" + currentStar);

                            }
                            rs1.close();
                            ps1.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    //System.out.println("EXCEPTION \"" + tempVal + "\" - invalid star data for \"" + currentMovie + "\"");

                }
            }
        }
        else if(qName.equalsIgnoreCase("filmc")){
//            for(Pair<String, String> p : listRelations){
//                System.out.println("please: "+p.getL()+ " "+ p.getR());
//            }
            currentMovie = "";
            currentMovieId="";
            currentPair = new Pair("", "");
           // System.out.println("size: "+listRelations.size());

        }
        else if(qName.equalsIgnoreCase("casts")){
            System.out.println("no");
            try {
//                for(Pair<String, String> p : listRelations){
//                System.out.println("please: "+p.getL()+ " "+ p.getR());
//                }
                printData(listRelations);
            } catch (Exception e) {
               // e.printStackTrace();
            }

        }
    }



    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                "mytestuser", "mypassword");
        if (connection != null) {
            System.out.println("Connection established!!");
        }

        SAXParserCast spe = new SAXParserCast();
        spe.runExample();

        connection.close();

    }

}

