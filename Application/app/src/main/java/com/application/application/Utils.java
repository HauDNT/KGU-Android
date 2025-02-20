package com.application.application;

import android.graphics.Color;
import android.widget.TextView;

import com.application.application.database.enums.OrderStatus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    //Phương thức mã hóa mật khẩu bằng SHA-256
    public static String hashPassword(String password) {
        try {
            //Khởi tạo MessageDigest với thuật toán SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //Mã hóa mật khẩu và trả về mảng byte
            byte[] hashBytes = digest.digest(password.getBytes());

            //Chuyển đổi mảng byte thành chuỗi hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            //Trả về mật khẩu đã mã hóa dưới dạng chuỗi hex
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hàm kiểm tra chuỗi hợp lệ theo Regex
    public static Boolean regexVerify(String text, String regex) {
        if (text.length() > 0) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text.trim());
            return matcher.matches();
        }
        return false;
    }

    // Hàm lấy thời gian hiện tại
    public static String getCurrentTime() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
        String currentTime = format.format(now);

        return currentTime;
    }

    //Phương thức kiểm tra tính hợp lệ của mật khẩu
    public static boolean isValidPassword(String password) {
        //Kiểm tra độ dài mật khẩu tối thiểu là 8 ký tự
        if (password.length() < 8) return false;
        //Kiểm tra mật khẩu có ít nhất một chữ hoa, một chữ thường và một số
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    //Phương thức kiểm tra tính hợp lệ của email
    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return regexVerify(email, emailPattern);
    }

    // Hàm set status của đơn hàng vào text view
    public static void setOrderStatus(OrderStatus orderStatus, TextView statusField) {
        switch (orderStatus) {
            case PENDING:
                statusField.setText("Đang xử lý");
                statusField.setTextColor(Color.BLUE);
                break;
            case DELIVERED:
                statusField.setText("Đã giao");
                statusField.setTextColor(Color.GREEN);
                break;
            case CANCELLED:
                statusField.setText("Đã huỷ");
                statusField.setTextColor(Color.RED);
                break;
            default:
                statusField.setText("Không xác định");
                break;
        }
    }

    public static String convertStringDateValid(String dateString, String inputFormat, String outputFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return ""; // Trả về chuỗi rỗng nếu giá trị đầu vào không hợp lệ
        }

        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);

        try {
            Date date = inputFormatter.parse(dateString); // Chuyển String thành Date
            return outputFormatter.format(date); // Chuyển Date thành String với định dạng mới
        } catch (ParseException e) {
            e.printStackTrace(); // Debug lỗi nếu có vấn đề với định dạng
            return dateString; // Trả về giá trị gốc nếu lỗi xảy ra
        }
    }

    // Hàm format lại tiền tệ theo VND
    public static String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
