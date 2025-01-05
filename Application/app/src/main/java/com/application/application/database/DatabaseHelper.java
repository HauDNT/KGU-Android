package com.application.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.application.application.BuildConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = BuildConfig.DATABASE_NAME;
    private static final int DATABASE_VERSION = Integer.parseInt(BuildConfig.DATABASE_VERSION);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username VARCHAR(16) UNIQUE NOT NULL,"
                + "password VARCHAR(255) NOT NULL,"
                + "fullname VARCHAR(255) UNIQUE,"
                + "email VARCHAR(255),"
                + "phone_number VARCHAR(15),"
                + "address VARCHAR(255),"
                + "is_admin INTEGER NOT NULL,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP"

                + ");";
        db.execSQL(createUsersTable);

        String createFoodsTable = "CREATE TABLE IF NOT EXISTS foods ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name NVARCHAR(255) NOT NULL,"
                + "description TEXT,"
                + "price REAL NOT NULL,"
                + "status INTEGER NOT NULL,"
                + "image_url TEXT,"
                + "created_at DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DEFAULT CURRENT_TIMESTAMP"
                + ");";
        db.execSQL(createFoodsTable);

        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name NVARCHAR(255) NOT NULL,"
                + "created_at DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DEFAULT CURRENT_TIMESTAMP"
                + ");";
        db.execSQL(createCategoriesTable);

        String createFoodCategoryTable = "CREATE TABLE IF NOT EXISTS food_category ("
                + "food_id INTEGER,"
                + "category_id INTEGER,"
                + "created_at DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (food_id, category_id),"
                + "FOREIGN KEY (food_id) REFERENCES foods(id),"
                + "FOREIGN KEY (category_id) REFERENCES categories(id)"
                + ");";
        db.execSQL(createFoodCategoryTable);

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "status INTEGER NOT NULL,"
                + "description TEXT,"
                + "delivery_at DEFAULT CURRENT_TIMESTAMP,"
                + "created_at DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DEFAULT CURRENT_TIMESTAMP,"
                + "user_id INTEGER,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");";
        db.execSQL(createOrdersTable);

        String createOrderItemTable = "CREATE TABLE IF NOT EXISTS order_item ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "order_id INTEGER,"
                + "food_id INTEGER,"
                + "quantity INTEGER NOT NULL,"
                + "total_price REAL NOT NULL,"
                + "FOREIGN KEY (food_id) REFERENCES foods(id),"
                + "FOREIGN KEY (order_id) REFERENCES orders(id)"
                + ");";
        db.execSQL(createOrderItemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_item");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS food_category");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS foods");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public long insertUser(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert("users", null, values);
    }

    public boolean isEmailValid(String email, String hashedPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, hashedPassword});

        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    public boolean isUserValid(String username, String hashedPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, hashedPassword});

        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }


}
