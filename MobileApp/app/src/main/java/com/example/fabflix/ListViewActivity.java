package com.example.fabflix;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        url = URLConstant.url;

        //this should be retrieved from the database and the backend server

        movies = new ArrayList<>();
        retrieveData();
//        Log.d("onCreate", "responses " + movies.toString());
//        Log.d("onCreate", "movies " + movies.toString());
        adapter = new MovieListAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

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
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final String urlParams = "title=&year=&director=&star=&gid=null&char=null&sort=null&num=null&firstRecord=null";
        final StringRequest retrieveRequest = new StringRequest(Request.Method.GET, url + "top20?" + urlParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("retrieve.success", response);
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for(int i = 0; i < jsonResponse.length(); ++i){
                        JSONObject object = jsonResponse.getJSONObject(i);
                        if(object.getString("result").equals("success")){
                            short year = (short) Integer.parseInt(object.getString("year"));
                            movies.add(new Movie(object.getString("title"), year));
                        }
                    }
                    Log.d("onResponse", movies.toString());
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