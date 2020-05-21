import java.util.ArrayList;
import java.util.List;

public class DirectorFilms {

    private String movieId;

    private String movieTitle = "";

    private int movieYear = 0;

    private String director = "";

    private ArrayList<Integer> movieGenres = new ArrayList<Integer>();


    public DirectorFilms(){
    }

    public int getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(int year) {
        this.movieYear = year;
    }

    public void setDirector(String d) {
        this.director = d;
    }

    public String getDirector() { return director; }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String title) {
        this.movieTitle = title;
    }

    public ArrayList<Integer> getMovieGenres() {
        return movieGenres;
    }

    public void addMovieGenre(int genre) {
        movieGenres.add(genre);
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
        s+= "Director: " + getDirector() + "\n";
        s+= "Genres: ";
        for(int g : getMovieGenres()){
            s += g +", ";
        }
        s += "\n";
        return s;
    }


}