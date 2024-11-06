package com.example.ambulanceapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AmbulanceAdapter extends CursorAdapter {

    public AmbulanceAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ambulance_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textCompanyName = view.findViewById(R.id.textCompanyName);
        ImageButton buttonCall = view.findViewById(R.id.buttonCall);

        // Retrieve data from the cursor
        String companyName = cursor.getString(cursor.getColumnIndexOrThrow("company_name"));
        final String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

        // Set the ambulance name
        textCompanyName.setText(companyName);

        // Set up call button to dial the ambulance's phone number
        buttonCall.setOnClickListener(v -> {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } else {
                Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
