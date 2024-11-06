package com.example.ambulanceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class UserSearchActivity extends BaseActivity {

    private DbConector dbHelper;
    private EditText editSearch;
    private Spinner spinnerFilterType;
    private ListView listView;
    private AmbulanceAdapter ambulanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        dbHelper = new DbConector(this);
        editSearch = findViewById(R.id.editSearch);
        spinnerFilterType = findViewById(R.id.spinnerFilterType);
        listView = findViewById(R.id.listView);

        // Load all ambulances initially
        loadAmbulances("");

        // Listen for changes in the search box
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Load ambulances based on search input and selected filter
                loadAmbulances(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAmbulances(String query) {
        // Get the selected filter type
        String filterType = spinnerFilterType.getSelectedItem().toString();

        // Query the database based on the selected filter and search input
        Cursor cursor = dbHelper.searchAmbulancesByFilter(filterType, query);

        // Initialize or update the adapter with the new cursor
        if (ambulanceAdapter == null) {
            ambulanceAdapter = new AmbulanceAdapter(this, cursor);
            listView.setAdapter(ambulanceAdapter);
        } else {
            ambulanceAdapter.changeCursor(cursor);
        }
    }
}
