package com.application.application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
