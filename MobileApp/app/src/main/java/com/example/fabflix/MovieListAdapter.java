package com.example.fabflix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class MovieListAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);
        TextView directorView = view.findViewById(R.id.director);
        TextView genresView = view.findViewById(R.id.genres);
        TextView starsView = view.findViewById(R.id.stars);


        titleView.setText(movie.getName());
        subtitleView.setText(movie.getYear() + "");// need to cast the year to a string to set the label
        directorView.setText(movie.getDirector());
        genresView.setText(movie.getGenres());
        starsView.setText(movie.getStars());

        return view;
    }
}