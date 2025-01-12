package com.application.application.activity.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
}
