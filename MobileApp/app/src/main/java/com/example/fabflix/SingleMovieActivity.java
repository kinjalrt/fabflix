package com.example.fabflix;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleMovieActivity extends AppCompatActivity {
    private String id;
    private String url;
    TextView movieName;
    TextView year;
    TextView director;
    TextView stars;
    TextView genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.d("Movie Id:",id);

        url = URLConstant.url;

        movieName = findViewById(R.id.movieName);
        year = findViewById(R.id.year);
        director = findViewById(R.id.director);
        stars = findViewById(R.id.stars);
        genres = findViewById(R.id.genres);

        retrieveMovie();

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void retrieveMovie() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest retrieveRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parse the json response to redirect to appropriate functions.
                Log.d("retrieveMovie.success", response);
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject jsonMovie = jsonResponse.getJSONObject(0);

                    String genresString = "";
                    String starsString = "";
                    for(int i = 0; i < jsonMovie.getInt("genre_count"); ++i){
                        if(i!=0)
                            genresString += ", ";
                        genresString += jsonMovie.getString("movie_genre" + (i + 1));
                    }
                    for(int i = 0; i < jsonMovie.getInt("stars_count"); ++i) {
                        if(i!=0)
                            starsString += "\n";
                        starsString += jsonMovie.getString("movie_stars" + (i + 1));
                    }

                    movieName.setText(jsonMovie.getString("movie_title"));
                    year.setText(jsonMovie.getString("movie_year"));
                    director.setText(jsonMovie.getString("movie_director"));
                    genres.setText(genresString);
                    stars.setText(starsString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("retrieveMovie.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(retrieveRequest);
    }
}
