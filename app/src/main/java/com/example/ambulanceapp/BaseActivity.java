package com.example.ambulanceapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from XML resource file
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_main_activity) {
            // Navigate to Main Activity
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.action_admin_login) {
            // Navigate to Admin Login Activity
            startActivity(new Intent(this, AdminLoginActivity.class));
            return true;
        } else if (id == R.id.action_register) {
            // Navigate to Register Activity
            startActivity(new Intent(this, AdminRegistrationActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            // Handle logout action, clear session or data as necessary
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back to the login screen or MainActivity
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
