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
import com.application.application.model.Category;
import com.application.application.model.Food;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP"
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
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
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

    //Thêm sản phẩm
    public long insertFood(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert("foods", null, values);
    }

    //Sửa sản phẩm
    public int updateFood(int id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update("foods", values, "id = ?", new String[]{String.valueOf(id)});
    }

    //Xóa sản phẩm
    public void deleteFood(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("foods", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Food> getAllFoods() {
        List<Food> foodList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // Lấy cơ sở dữ liệu đọc
        Cursor cursor = null;

        try {
            //Truy vấn tất cả các bản ghi từ bảng foods
            cursor = db.query("foods", null, null, null, null, null, null);

            //Kiểm tra xem con trỏ có dữ liệu hay không
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //Lấy dữ liệu từ con trỏ
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                    //Tạo đối tượng Food và thêm vào danh sách
                    Food food = new Food(id, name, description, price, status, imageUrl);
                    foodList.add(food);
                } while (cursor.moveToNext()); //Di chuyển đến bản ghi tiếp theo
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all foods: " + e.getMessage());
        } finally {
            //Đóng con trỏ nếu nó không null
            if (cursor != null) {
                cursor.close();
            }
        }

        return foodList; //Trả về danh sách các món ăn
    }

    // Lấy danh sách danh mục
    public List<Category> getCategoriesList(String[] columns) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (
                Cursor cursor = db.query(
                        "categories",
                        columns,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Integer id = null;
                    String name = null;
                    String created_at = null;
                    String updated_at = null;

                    for (String column : columns) {
                        switch (column) {
                            case "id":
                                id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                                break;
                            case "name":
                                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                                break;
                            case "created_at":
                                created_at = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                                break;
                            case "updated_at":
                                updated_at = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"));
                                break;
                        }
                    }

                    if (created_at == null & updated_at == null) {
                        categories.add(new Category(id, name));
                    } else {
                        categories.add(new Category(id, name, created_at, updated_at));
                    }
                }
            }
            else {
                categories.add(new Category(0, "Không có dữ liệu"));
            }
        } catch (Exception e) {
            Log.e("CategoryActivity", "Lỗi khi tải danh sách loại sản phẩm", e);
        } finally {
            db.close();
        }

        return categories;
    }

    // Hàm kiểm tra tồn tại theo 1 điều kiện:
    public Boolean isExistsCategory(String selection, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;
        Cursor cursor;

        if (selection.equals("id")) {
            cursor = db.query("categories", null, selection + " = ?", new String[]{value}, null, null, null);
        } else {
            cursor = db.query("categories", null, selection + " COLLATE NOCASE = ?", new String[]{value}, null, null, null);
        }

        if (cursor != null) {
            exists = cursor.moveToFirst();
        }

        cursor.close();
        return exists;
    }

    // Hàm thêm một danh mục mới
    public long insertCategory(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("categories", null, values);
        db.close();

        return result;
    }

    // Hàm xoá một danh mục
    public long deleteCategory(int id) {
        boolean checkExists = isExistsCategory("id", String.valueOf(id));
        SQLiteDatabase db = this.getReadableDatabase();

        if (checkExists) {
            long result = db.delete("categories", "id = ?", new String[]{String.valueOf(id)});
            db.close();
            return result;
        }

        return 0;
    }
}
