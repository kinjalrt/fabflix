
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
    private void printData(ArrayList<DirectorFilms> batch) throws Exception {

        // for each film:
            // check if year and genre and title all exist - done
            // if so get highest movie id and create new movie id
            // insert in movies tables
            // deal with genres_in_movies



        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord="INSERT INTO movies (id,title,year,director) VALUES(?, ?, ?, ?)";

        PreparedStatement psInsertRecordGenres =null;
        String sqlInsertRecordGenres = "INSERT INTO genres_in_movies (genreId,movieId) SELECT ?, ? FROM DUAL" +
                " WHERE NOT EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movieId = ? LIMIT 1);";
              //  "INSERT INTO genres_in_movies_test (genreId,movieId) values(?, ?)";


        //get max id in db
        String biggestId = "";
        String maxIdQuery = "select substring(max(id), 3) as id from movies;";
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
            psInsertRecordGenres=connection.prepareStatement(sqlInsertRecordGenres);


            for(DirectorFilms film : batch){
               // System.out.println("pls " + film.toString());
                String title = film.getMovieTitle();
                int year = film.getMovieYear();
                String director = film.getDirector();


                //create new movie id
                maxId = maxId + 1;
                String movieId = "tt"+(maxId);

                //insert movie
                psInsertRecord.setString(1, movieId);
                psInsertRecord.setString(2, title);
                psInsertRecord.setInt(3, year);
                psInsertRecord.setString(4, director);
                psInsertRecord.addBatch();

                //insert genre-movie
                for(int g : film.getMovieGenres()) {
                    psInsertRecordGenres.setInt(1, g);
                    psInsertRecordGenres.setString(2, movieId);
                    psInsertRecordGenres.setInt(3, g);
                    psInsertRecordGenres.setString(4, movieId);
                    psInsertRecordGenres.addBatch();

                }

              //  psInsertRecordGenres.executeBatch();


            }
            psInsertRecord.executeBatch();
            psInsertRecordGenres.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            System.out.println("kenma"+e.getMessage());//e.printStackTrace();

        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            if(psInsertRecordGenres!=null) psInsertRecordGenres.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("directorfilms")) {

            //array of all films for current director
            director = "null";
            if(listDirectorFilms.size()>5){
                try {
                    printData(listDirectorFilms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listDirectorFilms = new ArrayList<DirectorFilms>();
            }
        }
        else if(qName.equalsIgnoreCase("film")){
            tempMovie = new DirectorFilms();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        try(FileWriter fw = new FileWriter("ParserMovies.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            if (qName.equalsIgnoreCase("t")) {
                tempMovie.setMovieTitle(tempVal);
                tempMovie.setDirector(director);
            }
            else if (qName.equalsIgnoreCase("year")) {
                try {
                    tempMovie.setMovieYear(Integer.parseInt(tempVal));
                }catch (Exception e){
                    //specify for which movie?
                    out.println("Invalid year data \"" + tempVal + "\" for movie \""+ tempMovie.getMovieTitle()+"\"");
                  //  System.out.println("EXCEPTION - invalid movie year data for : " + tempVal);
                }
            }
            else if (qName.equalsIgnoreCase("cat")) {
                if(!tempVal.equals("")) {
                    int g = translateGenre(tempVal);
                    if(g!=0){
                        tempMovie.addMovieGenre(g);

                    }
                    else{
                        out.println("Genre \""+ tempVal +"\" unrecognizable for movie \""+ tempMovie.getMovieTitle()+"\"");
                      //  System.out.println("Genre type invalid for " + tempMovie.getMovieTitle());
                    }
                }

            }
            else if(qName.equalsIgnoreCase("dirname")){
                director = tempVal;
                //tempMovie.setDirector(director);
            }
            else if (qName.equalsIgnoreCase("film")){
              //  System.out.println("add" + tempMovie.getMovieTitle() + " "+ tempMovie.getDirector());

                //check if movie has >= 1 genre and year!=0
                    if((!tempMovie.getDirector().equals("null")) && tempMovie.getMovieGenres().size()>0 && tempMovie.getMovieYear()!=0 && (!tempMovie.getMovieTitle().equals("NKT"))) {
                        try {
                            //check if movie in db
                            String mid = "";
                            String query0 = "select id from movies where title = ? and year = ? and director = ?;";
                            PreparedStatement ps0 = connection.prepareStatement(query0);
                            ps0.setString(1, tempMovie.getMovieTitle());
                            ps0.setInt(2, tempMovie.getMovieYear());
                            ps0.setString(3, tempMovie.getDirector());
                            ResultSet rs0 = ps0.executeQuery();
                            if (rs0.next()) {
                                out.println("Movie \""+ tempMovie.getMovieTitle()+"\" already in database");
                             //   System.out.println("movie already in db");
                             //   System.out.println(tempMovie.toString());
                                mid = rs0.getString("id");
                                //add missing genres if any
                                for(int g : tempMovie.getMovieGenres()) {
                                    //SET PRIMARY KEYS FOR BOTH IDS IN GENRES_IN_MOVIES
                                    try{
                                        String query10 = "INSERT INTO genres_in_movies (genreId,movieId) VALUES (?, ?);";
                                        PreparedStatement ps10 = connection.prepareStatement(query10);
                                        ps10.setInt (1, g);
                                        ps10.setString (2, mid);
                                        ps10.execute();
                                        ps10.close();
                                       // System.out.println(g+ " "+ mid);

                                    } catch (SQLException e) {
                                       // System.out.println("nagisa"+e.getMessage());
                                       // e.printStackTrace();
                                    }

                                }
                               // System.out.println();

                            } else {
                                listDirectorFilms.add(tempMovie);
                               // System.out.println("adding : "+ tempMovie.toString());
                            }
                            rs0.close();
                            ps0.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        if(tempMovie.getDirector().equals("null")){
                            out.println("No director associated with movie \""+ tempMovie.getMovieTitle()+"\"");
                        }else if (tempMovie.getMovieGenres().size()==0){
                            out.println("Zero genre associated with movie \""+ tempMovie.getMovieTitle()+"\"; movie should have at least 1 genre");
                        } else if (tempMovie.getMovieTitle().equals("NKT")){
                            out.println("NKT - movie title unknown");
                        }
                    }
            }

            else if(qName.equalsIgnoreCase("movies")){
                try {
                    printData(listDirectorFilms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private int translateGenre(String genreParam){

        String genre = genreParam.trim();
        //add genre to genres tables if not exist
        if(genre.equals("CnR") || genre.equals("Noir") || genre.equals("TVm") || genre.equals("TV") || genre.equals("TVs")){
            try {
                String query5 = "select * from genres where name = ?;";
                PreparedStatement ps5 = connection.prepareStatement(query5);
                ps5.setString(1, genre);
                ResultSet rs5 = ps5.executeQuery();
                int genreId = 0;
                if (rs5.next()) {
                    // System.out.println("GENRE " + genreId);
                }else{
                    //if genre not in genres table
                    String query4 = "INSERT INTO genres (name) VALUES (?);";
                    PreparedStatement ps4 = connection.prepareStatement(query4);
                    ps4.setString (1, genre);
                    ps4.execute();
                    ps4.close();

                }
                rs5.close();
                ps5.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String translatedGenre = "";

        if(genre.equals("Susp")) {
            translatedGenre = "Thriller";
        }
        else if(genre.equals("CnR")){
            translatedGenre = "Cops and Robbers";
        }
        else if(genre.equals("Dram")){
            translatedGenre = "Drama";
        }
        else if(genre.equals("West")){
            translatedGenre = "Western";
        }
        else if(genre.equals("Myst")){
            translatedGenre = "Mystery";
        }
        else if(genre.equals("S.F.") || genre.equals("ScFi")){
            translatedGenre = "Sci-Fi";
        }
        else if(genre.equals("Advt")){
            translatedGenre = "Adventure";
        }
        else if(genre.equals("Actn")){
            translatedGenre = "Action";
        }
        else if(genre.equals("Horr")){
            translatedGenre  = "Horror";
        }
        else if(genre.equals("Romt")){
            translatedGenre = "Romance";
        }
        else if(genre.equals("Comd")){
            translatedGenre = "Comedy";
        }
        else if(genre.equals("Musc")){
            translatedGenre = "Musical";
        }
        else if(genre.equals("Docu")){
            translatedGenre = "Documentary";
        }
        else if(genre.equals("Porn")){
            translatedGenre = "Adult";
        }
        else if (genre.equals("Noir")){
            translatedGenre = "Black";
        }
        else if(genre.equals("BioP")){
            translatedGenre = "Biography";
        }
        else if(genre.equals("TV")){
            translatedGenre = "TV-Show";
        }
        else if(genre.equals("TVs")){
            translatedGenre = "TV-Series";
        }
        else if(genre.equals("TVm")){
            translatedGenre = "TV-Miniseries";
        }

        //return associated genre id
        int genreId = 0;
        try {
            String query7 = "select id from genres where name = ?;";
            PreparedStatement ps7 = connection.prepareStatement(query7);
            ps7.setString(1, translatedGenre);
            ResultSet rs7 = ps7.executeQuery();
            while (rs7.next()) {
                genreId = rs7.getInt("id");
              //  System.out.println("GENRE " + genreId);
            }
            rs7.close();
            ps7.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return genreId;

    }





    public static void main(String[] args) throws Exception {

        try {
            File file = new File("ParserMovies.txt");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "mypassword");
            if (connection != null) {
                System.out.println("Connection established!!");
            }

            SAXParserMovies spe = new SAXParserMovies();
            spe.runExample();

            connection.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

