package com.example.ambulanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // function redirects to ambulance search
    public void openUser(View view) {
        startActivity(new Intent(MainActivity.this, UserSearchActivity.class));
    }

}