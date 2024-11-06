package com.example.ambulanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConector extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ambulanceApp.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_ADMIN = "admin";
    private static final String TABLE_AMBULANCE = "ambulance";

    // Admin Table Columns
    private static final String COLUMN_ADMIN_ID = "id";
    private static final String COLUMN_ADMIN_USERNAME = "username";
    private static final String COLUMN_ADMIN_PASSWORD = "password";

    // Ambulance Table Columns
    private static final String COLUMN_AMBULANCE_ID = "id";
    private static final String COLUMN_AMBULANCE_COMPANY_NAME = "company_name";
    private static final String COLUMN_AMBULANCE_LOCATION = "location";
    public static final String COLUMN_AMBULANCE_LANDMARK = "landmark";
    public static final String COLUMN_AMBULANCE_VEHICLE_TYPE = "vehicle_type";
    private static final String COLUMN_AMBULANCE_PHONE = "phone";
    private static final String COLUMN_AMBULANCE_ADMIN_ID = "admin_id";

    public DbConector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Admin Table
        String CREATE_ADMIN_TABLE = "CREATE TABLE " + TABLE_ADMIN + "("
                + COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADMIN_USERNAME + " TEXT, "
                + COLUMN_ADMIN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_ADMIN_TABLE);

        // Create Ambulance Table
        String CREATE_AMBULANCE_TABLE = "CREATE TABLE " + TABLE_AMBULANCE + "("
                + COLUMN_AMBULANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_AMBULANCE_COMPANY_NAME + " TEXT, "
                + COLUMN_AMBULANCE_LOCATION + " TEXT, "
                + COLUMN_AMBULANCE_LANDMARK + " TEXT, "
                + COLUMN_AMBULANCE_VEHICLE_TYPE + " TEXT, "
                + COLUMN_AMBULANCE_PHONE + " TEXT,"
                + COLUMN_AMBULANCE_ADMIN_ID + " INTEGER, "
                + "FOREIGN KEY (" + COLUMN_AMBULANCE_ADMIN_ID + ") REFERENCES " + TABLE_ADMIN + "(" + COLUMN_ADMIN_ID + ")" + ")";
        db.execSQL(CREATE_AMBULANCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AMBULANCE);
        onCreate(db);
    }

    // Insert a new Admin
    public boolean insertAdmin(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADMIN_USERNAME, username);
        values.put(COLUMN_ADMIN_PASSWORD, password);
        long result = db.insert(TABLE_ADMIN, null, values);
        return result != -1;
    }

    // Check Admin Login
    public boolean checkAdminLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADMIN + " WHERE "
                + COLUMN_ADMIN_USERNAME + "=? AND " + COLUMN_ADMIN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    //   Get admin Id
    public int getAdminId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ADMIN_ID + " FROM " + TABLE_ADMIN +
                        " WHERE " + COLUMN_ADMIN_USERNAME + " = ? AND " + COLUMN_ADMIN_PASSWORD + " = ?",
                new String[]{username, password});

        int adminId = -1; // Default to -1 if not found
        if (cursor.moveToFirst()) {
            // Safely get the column index and check for -1
            int columnIndex = cursor.getColumnIndex(COLUMN_ADMIN_ID);
            if (columnIndex != -1) {
                adminId = cursor.getInt(columnIndex);
            }
        }
        cursor.close();
        return adminId;
    }


    // Insert Ambulance Details
    public long insertAmbulance(int adminId, String companyName, String location, String landmark, String vehicleType, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMBULANCE_ADMIN_ID, adminId); // Assuming you have an admin_id column
        values.put(COLUMN_AMBULANCE_COMPANY_NAME, companyName);
        values.put(COLUMN_AMBULANCE_LOCATION, location);
        values.put(COLUMN_AMBULANCE_LANDMARK, landmark);
        values.put(COLUMN_AMBULANCE_VEHICLE_TYPE, vehicleType);
        values.put(COLUMN_AMBULANCE_PHONE, phone);
        return db.insert(TABLE_AMBULANCE, null, values);
    }

    // Retrieve ambulances associated with a specific admin
    public Cursor getAmbulancesByAdminId(int adminId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Convert adminId to a String array
        String[] selectionArgs = new String[]{String.valueOf(adminId)};
        return db.query(TABLE_AMBULANCE, null, COLUMN_AMBULANCE_ADMIN_ID + " = ?", selectionArgs, null, null, null);
    }

    // Update ambulance details
    public boolean updateAmbulance(int id, String companyName, String location, String landmark, String vehicleType, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMBULANCE_COMPANY_NAME, companyName);
        values.put(COLUMN_AMBULANCE_LOCATION, location);
        values.put(COLUMN_AMBULANCE_LANDMARK, landmark);
        values.put(COLUMN_AMBULANCE_VEHICLE_TYPE, vehicleType);
        values.put(COLUMN_AMBULANCE_PHONE, phone);

        // Update the ambulance record in the database
        int rowsAffected = db.update(TABLE_AMBULANCE, values, COLUMN_AMBULANCE_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0; // Return true if the update was successful
    }

    // delete ambulance
    public boolean deleteAmbulance(int ambulanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_AMBULANCE, COLUMN_AMBULANCE_ID + " = ?", new String[]{String.valueOf(ambulanceId)}) > 0;
    }

    // filter ambulances by, loaction, landmark, vehicle_type, phone, company_name
    public Cursor searchAmbulancesByFilter(String filterType, String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Map filter types to column names in the database
        String column;
        switch (filterType) {
            case "Location":
                column = "location";
                break;
            case "Landmark":
                column = "landmark";
                break;
            case "Vehicle Type":
                column = "vehicle_type";
                break;
            case "Phone":
                column = "phone";
                break;
            default:
                column = "company_name"; // Default to company name
                break;
        }

        // Prepare the SQL query with `id` as `_id`
        String sql = "SELECT id AS _id, company_name, location, landmark, vehicle_type, phone, admin_id " +
                "FROM " + TABLE_AMBULANCE + " WHERE " + column + " LIKE ?";
        String likeQuery = "%" + query + "%";
        return db.rawQuery(sql, new String[]{likeQuery});
    }

}


