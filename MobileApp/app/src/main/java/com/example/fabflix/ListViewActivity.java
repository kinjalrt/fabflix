package com.example.fabflix;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ListViewActivity extends Activity {
    private String url;
    private ArrayList<Movie> movies = new ArrayList<>();
    private MovieListAdapter adapter;
    private int firstRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        url = URLConstant.url;

        //data retrieved from the database and the backend server
        movies = new ArrayList<>();
        firstRecord = 0;
        Button prevButton = findViewById(R.id.prev);
        Button nextButton = findViewById(R.id.next);

        retrieveData();
        adapter = new MovieListAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        //on click on the prev button
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstRecord == 0){
                    String message = "Already on first page";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    firstRecord -= 20;
                    if (firstRecord < 0) {
                        firstRecord = 0;
                    }
                    //refresh displayed data
                    retrieveData();
                }

            }
        });
        //on click on the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstRecord += 20;
                //refresh displayed data
                retrieveData();
                if(!movies.isEmpty()){
                    String message = "End of the result";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
        //on click on the movie
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void retrieveData(){
        //reset the old data
        if(!movies.isEmpty()) {
            movies.clear();
        }
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final String urlParams = URLConstant.searchParams + firstRecord;
        final StringRequest retrieveRequest = new StringRequest(Request.Method.GET, url + "top20?" + urlParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parse the json response to redirect to appropriate functions.
                Log.d("retrieve.success", response);
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for(int i = 0; i < jsonResponse.length(); ++i){
                        JSONObject object = jsonResponse.getJSONObject(i);
                        if(object.getString("result").equals("success")){
                            String title = object.getString("title");
                            short year = (short) Integer.parseInt(object.getString("year"));
                            String director = object.getString("dir");
                            String stars = "";
                            String genres = "";
                            for(int j = 1; j < 4; ++j){
                                if(object.has("genre"+j)) {
                                    if(j != 1)
                                        genres += ", " + object.getString("genre" + j);
                                    else
                                        genres += object.getString("genre" + j);
                                }
                                if(object.has("starname"+j)) {
                                    if(j != 1)
                                        stars += ", " + object.getString("starname" + j);
                                    else
                                        stars += object.getString("starname" + j);
                                }
                            }
                            movies.add(new Movie(title, year, director, stars, genres));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("retrieve.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(retrieveRequest);
    }
}