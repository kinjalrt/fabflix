import java.util.ArrayList;
import java.util.List;

public class DirectorFilms {

    private String movieId;

    private String movieTitle = "";

    private int movieYear = 0;

    private ArrayList<String> movieGenres = new ArrayList<String>();


    public DirectorFilms(){
    }

    public int getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(int year) {
        this.movieYear = year;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String title) {
        this.movieTitle = title;
    }

    public ArrayList<String> getMovieGenres() {
        return movieGenres;
    }

    public void addMovieGenre(String genre) {
        if(genre.equals("Susp")) {
            movieGenres.add("Thriller");
        }
        else if(genre.equals("CnR")){
            movieGenres.add("Cops and Robbers");
        }
        else if(genre.equals("Dram")){
            movieGenres.add("Drama");
        }
        else if(genre.equals("West")){
            movieGenres.add("Western");
        }
        else if(genre.equals("Myst")){
            movieGenres.add("Mystery");
        }
        else if(genre.equals("S.F.")){
            movieGenres.add("Sci-Fi");
        }
        else if(genre.equals("Advt")){
            movieGenres.add("Adventure");
        }
        else if(genre.equals("Horr")){
            movieGenres.add("Horror");
        }
        else if(genre.equals("Romt")){
            movieGenres.add("Romance");
        }
        else if(genre.equals("Comd")){
            movieGenres.add("Comedy");
        }
        else if(genre.equals("Musc")){
            movieGenres.add("Musical");
        }
        else if(genre.equals("Docu")){
            movieGenres.add("Documentary");
        }
        else if(genre.equals("Porn")){
            movieGenres.add("Adult");
        }
        else if (genre.equals("Noir")){
            movieGenres.add("Black");
        }
        else if(genre.equals("BioP")){
            movieGenres.add("Biography");
        }
        else if(genre.equals("TV")){
            movieGenres.add("TV-Show");
        }
        else if(genre.equals("TVs")){
            movieGenres.add("TV-Series");
        }
        else if(genre.equals("TVm")){
            movieGenres.add("TV-Miniseries");
        }
    }

    public String getId() {
        return movieId;
    }

    public void setMovieId(String id) {
        this.movieId = id;
    }

    public String toString() {
        String s = "Title:" + getMovieTitle() + "\n";
        s += "Year:" + getMovieYear() + "\n";
        s+= "Genres: ";
        for(String g : getMovieGenres()){
            s += g +", ";
        }
        return s;
    }


}