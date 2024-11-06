package com.example.ambulanceapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminDashboardActivity extends BaseActivity {

    private ArrayList<HashMap<String, String>> ambulanceList; // List of ambulances using HashMap
    private ArrayAdapter<String> listViewAdapter;
    private ListView listView;
    private EditText editCompanyName, editLocation, editLandmark, editVehicleType, editPhone;
    private Button buttonAddAmbulance;
    private DbConector dbHelper; // Database helper to interact with SQLite
    private int editingIndex = -1; // Track index of ambulance being edited
    private int adminId; // Store admin ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize database helper
        dbHelper = new DbConector(this);

        // Retrieve admin_id from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        adminId = prefs.getInt("admin_id", -1);

        // Initialize views
        listView = findViewById(R.id.listView);
        editCompanyName = findViewById(R.id.editCompanyName);
        editLocation = findViewById(R.id.editLocation);
        editLandmark = findViewById(R.id.editLandmark);
        editVehicleType = findViewById(R.id.editVehicleType);
        editPhone = findViewById(R.id.editPhone);
        buttonAddAmbulance = findViewById(R.id.buttonAddAmbulance);

        // Initialize the ambulance list
        ambulanceList = new ArrayList<>();

        // Create an adapter to display ambulance company names in the ListView
        listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(listViewAdapter);

        // Load existing ambulances from the database
        loadAmbulances();

        // Add ambulance button functionality
        buttonAddAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateAmbulance();
            }
        });

        // Set item click listener to edit the selected ambulance
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editAmbulance(position);
            }
        });

        // Set item long click listener to delete the selected ambulance
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDeleteAmbulance(position);
                return true;
            }
        });
    }

    private String getStringSafely(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getString(index);
        } else {
            Log.e("DB_COLUMN", "Column not found: " + columnName);
            return ""; // Or return a default value
        }
    }

    // function for getting ambulance
    private void loadAmbulances() {
        ambulanceList.clear();
        listViewAdapter.clear();

        // Get ambulances associated with the current admin ID
        Cursor cursor = dbHelper.getAmbulancesByAdminId(adminId);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> ambulance = new HashMap<>();
                ambulance.put("id", getStringSafely(cursor, "id"));
                ambulance.put("company_name", getStringSafely(cursor, "company_name"));
                ambulance.put("location", getStringSafely(cursor, "location"));
                ambulance.put("landmark", getStringSafely(cursor, "landmark"));
                ambulance.put("vehicle_type", getStringSafely(cursor, "vehicle_type"));
                ambulance.put("phone", getStringSafely(cursor, "phone"));

                // Add to list and ListView adapter
                ambulanceList.add(ambulance);
                listViewAdapter.add(ambulance.get("company_name"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        listViewAdapter.notifyDataSetChanged();
    }

    // function for adding or updating ambulance
    private void addOrUpdateAmbulance() {
        String companyName = editCompanyName.getText().toString();
        String location = editLocation.getText().toString();
        String landmark = editLandmark.getText().toString();
        String vehicleType = editVehicleType.getText().toString();
        String phone = editPhone.getText().toString();

        if (editingIndex >= 0) {
            // Update existing ambulance
            HashMap<String, String> ambulance = ambulanceList.get(editingIndex);
            dbHelper.updateAmbulance(
                    Integer.parseInt(ambulance.get("id")),
                    companyName, location, landmark, vehicleType, phone
            );

            ambulance.put("company_name", companyName);
            ambulance.put("location", location);
            ambulance.put("landmark", landmark);
            ambulance.put("vehicle_type", vehicleType);
            ambulance.put("phone", phone);
            listViewAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Ambulance Updated", Toast.LENGTH_SHORT).show();
            editingIndex = -1; // Reset editing index
        } else {
            // Add new ambulance
            long id = dbHelper.insertAmbulance(adminId, companyName, location, landmark, vehicleType, phone);
            if (id != -1) {
                HashMap<String, String> ambulance = new HashMap<>();
                ambulance.put("id", String.valueOf(id));
                ambulance.put("company_name", companyName);
                ambulance.put("location", location);
                ambulance.put("landmark", landmark);
                ambulance.put("vehicle_type", vehicleType);
                ambulance.put("phone", phone);
                ambulanceList.add(ambulance);

                listViewAdapter.add(companyName);
                listViewAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Ambulance Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Adding Ambulance", Toast.LENGTH_SHORT).show();
            }
        }

        clearFields();
    }

     // function for editing ambulance
    private void editAmbulance(int position) {
        HashMap<String, String> selectedAmbulance = ambulanceList.get(position);
        editCompanyName.setText(selectedAmbulance.get("company_name"));
        editLocation.setText(selectedAmbulance.get("location"));
        editLandmark.setText(selectedAmbulance.get("landmark"));
        editVehicleType.setText(selectedAmbulance.get("vehicle_type"));
        editPhone.setText(selectedAmbulance.get("phone"));
        editingIndex = position;
        Toast.makeText(this, "Edit the details and click Add", Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        editCompanyName.setText("");
        editLocation.setText("");
        editLandmark.setText("");
        editVehicleType.setText("");
        editPhone.setText("");
    }

    // function for confirming deletion ambulance
    private void confirmDeleteAmbulance(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ambulance")
                .setMessage("Are you sure you want to delete this ambulance?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAmbulance(position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }


    // function for deleting ambulance
    private void deleteAmbulance(int position) {
        HashMap<String, String> ambulance = ambulanceList.get(position);
        int ambulanceId = Integer.parseInt(ambulance.get("id"));
        if (dbHelper.deleteAmbulance(ambulanceId)) {
            ambulanceList.remove(position);
            listViewAdapter.remove(listViewAdapter.getItem(position));
            listViewAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Ambulance deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete ambulance", Toast.LENGTH_SHORT).show();
        }
    }
}
