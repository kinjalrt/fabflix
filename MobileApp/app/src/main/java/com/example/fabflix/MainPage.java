package com.example.fabflix;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainPage extends AppCompatActivity {
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        search = findViewById(R.id.searchBar);
        Log.d("search:", search.getText().toString());
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(MainPage.this, ListViewActivity.class);
                listPage.putExtra("search", search.getText().toString());
                startActivity(listPage);
            }
        });
    }
}
