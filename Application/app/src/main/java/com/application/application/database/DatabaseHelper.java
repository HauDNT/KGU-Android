package com.application.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.application.application.BuildConfig;
import com.application.application.database.enums.OrderStatus;
import com.application.application.model.Category;
import com.application.application.model.Food;
import com.application.application.model.Order;
import com.application.application.model.OrderItem;

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
                + "name NVARCHAR(255) NOT NULL,"
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

    // ---------------------------------------------------- Login & Register ----------------------------------------------------
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

    // ---------------------------------------------------- Foods ----------------------------------------------------
    public boolean isFoodExists(String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM foods WHERE name = ?", new String[]{foodName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    //Thêm món ăn mới
    public long insertFood(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;

        try {
            newRowId = db.insert("foods", null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        } finally {
            db.close(); // Đảm bảo đóng cơ sở dữ liệu
        }

        return newRowId;
    }

    //Cập nhật thông tin món ăn
    public int updateFood(int foodId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsUpdated = db.update("foods", values, "id = ?", new String[]{String.valueOf(foodId)});
        db.close();
        return rowsUpdated;
    }

    //Xóa món ăn
    public void deleteFood(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("foods", "id = ?", new String[]{String.valueOf(id)});
    }

    //Hàm truy xuất tất cả các món ăn từ cơ sở dữ liệu và trả về danh sách các đối tượng
    public List<Food> getAllFoods() {
        List<Food> foodList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("foods", null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url")); //Lấy đường dẫn hình ảnh

                    Food food = new Food(id, name, description, price, status, imageUrl);
                    foodList.add(food);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all foods: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return foodList;
    }

    // ---------------------------------------------------- Categories ----------------------------------------------------
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
            } else {
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
        db.close();
        return exists;
    }

    // Hàm thêm một danh mục mới
    public long insertCategory(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("categories", null, values);
        db.close();

        return result;
    }

    public long updateCategoryName(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.update("categories", values, "id = ?", new String[]{String.valueOf(values.get("id"))});
        return result;
    }

    // Hàm xoá một danh mục
    public long deleteCategory(int id) {
        boolean checkExists = isExistsCategory("id", String.valueOf(id));
        SQLiteDatabase db = this.getReadableDatabase();

        if (checkExists) {
            long result = db.delete("categories", "id = ?", new String[]{String.valueOf(id)});
            return result;
        }

        db.close();
        return 0;
    }

    //Hàm lấy id của category dựa trên name
    public long getCategoryIdByName(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM categories WHERE name = ?", new String[]{categoryName});
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return -1;
    }

    //Hàm thêm dữ liệu vào bảng food_category
    public long insertFoodCategory(long foodId, long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("food_id", foodId);
        values.put("category_id", categoryId);
        return db.insert("food_category", null, values);
    }

    //Hàm cập nhật lại danh mục loại sản phẩm cho món ăn
    public void updateFoodCategories(int foodId, List<String> selectedCategories) {
        SQLiteDatabase db = this.getWritableDatabase(); // Khởi tạo biến db

        //Xóa tất cả các loại hiện tại
        db.execSQL("DELETE FROM food_category WHERE food_id = ?", new String[]{String.valueOf(foodId)});

        //Thêm lại các loại đã chọn
        for (String category : selectedCategories) {
            long categoryId = getCategoryIdByName(category);
            if (categoryId != -1) {
                insertFoodCategory(foodId, categoryId);
            }
        }

        db.close();
    }

    //Hàm lấy danh sách loại sản phẩm theo id món ăn
    public List<String> getCategoriesForFood(long foodId) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.name AS category_name FROM food_category fc " +
                "JOIN categories c ON fc.category_id = c.id " +
                "WHERE fc.food_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(foodId)});

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("category_name");
                    if (columnIndex != -1) {
                        String categoryName = cursor.getString(columnIndex);
                        categories.add(categoryName);
                    } else {
                        Log.e("DatabaseHelper", "Column 'category_name' not found");
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return categories;
    }

    // ---------------------------------------------------- Order & Order Items ----------------------------------------------------
    // Hàm kiểm tra tồn tại giỏ hàng chưa?
    public Boolean isExistsOrder(String selection, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;
        Cursor cursor;

        if (selection.equals("id")) {
            cursor = db.query("orders", null, selection + " = ?", new String[]{value}, null, null, null);
        } else {
            cursor = db.query("orders", null, selection + " COLLATE NOCASE = ?", new String[]{value}, null, null, null);
        }

        if (cursor != null) {
            exists = cursor.moveToFirst();
        }

        cursor.close();
        db.close();
        return exists;
    }

    // Hàm lấy số lượng giỏ hàng trong Database
    public int getOrdersAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM orders", null);

        return cursor.getCount();
    }

    // Hàm lấy danh sách giỏ hàng
    public List<Order> getOrdersList() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM orders", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                OrderStatus orderStatus = OrderStatus.fromValue(status);

                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String delivery_at = cursor.getString(cursor.getColumnIndexOrThrow("delivery_at"));
                String created_at = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                String updated_at = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));

                orders.add(new Order(id, name, orderStatus, description, delivery_at, created_at, updated_at, userId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orders;
    }

    // Hàm tạo 1 giỏ hàng
    public long createOrder(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("orders", null, values);
        db.close();

        return result;
    }

    // Hàm kiểm tra xem đã có item (order id và food id trong bảng 'order_item') chưa?
    public OrderItem getExistOrderItem(int foodId, int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        OrderItem orderItem;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM order_item WHERE food_id = ? AND order_id = ?",
                new String[]{String.valueOf(foodId), String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int order_id = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
            int food_id = cursor.getInt(cursor.getColumnIndexOrThrow("food_id"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));

            orderItem = new OrderItem(id, food_id, order_id, quantity, totalPrice);

            return orderItem;
        }

        return null;
    }

    // Hàm tạo hoặc cập nhật thêm (nếu getExistOrderItem != null) item trong bảng 'order_item'
    public long addOrUpdateFoodToOrders(int foodId, List<Integer> ordersListIdSelected, int quantity) {
        long result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Lấy giá bán của sản phẩm
            Cursor cursor = db.rawQuery(
                    "SELECT price FROM foods WHERE id = ?", new String[]{String.valueOf(foodId)});
            float foodPrice = cursor.getColumnIndexOrThrow("price");

            // Thêm mới hoặc cập nhật giỏ hàng
            ContentValues values = new ContentValues();

            for (int i = 0; i < ordersListIdSelected.size(); i++) {
                OrderItem orderItem = getExistOrderItem(foodId, ordersListIdSelected.get(i));

                if (orderItem != null) {
                    int newQuantity = orderItem.getQuantity() + quantity;  // Tăng quantity lên 1
                    double newTotalPrice = orderItem.getTotalPrice() * newQuantity;

                    values.put("food_id", foodId);
                    values.put("order_id", ordersListIdSelected.get(i));
                    values.put("quantity", newQuantity);
                    values.put("total_price", newTotalPrice);

                    result = db.update(
                            "order_item",
                            values,
                            "food_id = ? AND order_id = ?",
                            new String[]{String.valueOf(foodId), String.valueOf(ordersListIdSelected.get(i))}
                    );
                } else {
                    values.put("food_id", foodId);
                    values.put("order_id", ordersListIdSelected.get(i));
                    values.put("quantity", quantity);
                    values.put("total_price", foodPrice * quantity);
                    result = db.insert("order_item", null, values);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error when add to orders", e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }

        return result;
    }

    public long deleteOrder(int orderId) {
        long result = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // Xoá đơn hàng từ bảng cha Orders
            result = db.delete("orders", "id = ?", new String[]{String.valueOf(orderId)});

            // Nếu thành công thì xoá luôn dữ liệu hiện có trong bảng Order_Item
            if (result > 0) {
                Cursor cursor = db.rawQuery(
                        "SELECT order_id FROM order_item WHERE order_id = ?", new String[]{String.valueOf(orderId)});

                if (cursor.moveToFirst()) {
                    result = db.delete("order_item", "order_id = ?", new String[]{String.valueOf(orderId)});
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error when delete order " + orderId, e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return result;
    }
}
