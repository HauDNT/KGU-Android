package com.application.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.application.application.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    Button btnInsert, btnQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        btnInsert = findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(v -> insertToDB());

        btnQuery = findViewById(R.id.btnQuery);
        btnQuery.setOnClickListener(v -> queryUsers());
    }

    private void insertToDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", "user1");
        values.put("password", "123456789");
        values.put("is_admin", false);
        db.insert("users", null, values);

        db.close();
    }

    private void queryUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            "id",          // Cột ID
            "username",    // Cột username
            "password",    // Cột password
            "fullname",    // Cột fullname
            "email",       // Cột email
            "phone_number",// Cột phone_number
            "address",     // Cột address
            "is_admin",    // Cột is_admin
            "created_at",
            "updated_at"
        };

        try (
                Cursor cursor = db.query(
                        "users",
                        columns,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("is_admin"));

                Log.d("MainActivity", "ID: " + id + ", Username: " + username + ", Fullname: " + fullname + ", Email: " + email);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error querying data", e);
        } finally {
            db.close(); // Đảm bảo đóng cơ sở dữ liệu
        }
    }
}