package com.example.demo.otherfunction;

import java.security.*;

public class encryption {

    // private static final String PREFIX = "12345";
    // private static final String SUFFIX = "09876";

    // public static String encrypt(String password) {
    // // Chuẩn bị chuỗi đã thêm tiền tố và hậu tố
    // String combinedPassword = PREFIX + password + SUFFIX;
    // try {
    // MessageDigest md = MessageDigest.getInstance("SHA-1");
    // byte[] byteData = md.digest(combinedPassword.getBytes());

    // // Chuyển đổi mảng byte thành chuỗi hex
    // StringBuilder sb = new StringBuilder();
    // for (byte b : byteData) {
    // sb.append(String.format("%02x", b));
    // }
    // // Trả về chuỗi mã hóa cuối cùng
    // return sb.toString();
    // } catch (NoSuchAlgorithmException e) {
    // throw new RuntimeException("SHA-1 algorithm not found!", e);
    // }
    // }

    private static final String SALT = "12345"; // You can replace this with a dynamic salt or use a unique salt per
                                                // password in a real scenario.

    // Encrypt password with SHA-1 and salt
    public static String encrypt(String password) {
        try {
            // Generate a random salt for each password (In a real app, this should be
            // unique for each password)
            byte[] salt = SALT.getBytes(); // Use a static or dynamic salt

            // Combine password with the salt
            String combinedPassword = password + new String(salt);

            // Hash the password with SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] byteData = md.digest(combinedPassword.getBytes());

            // Apply key stretching (apply multiple rounds of hashing)
            for (int i = 0; i < 1000; i++) { // Perform 1000 rounds of hashing (can adjust as needed)
                byteData = md.digest(byteData);
            }

            // Convert byte array to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b));
            }

            // Return the hashed password as a string
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found!", e);
        }
    }

    // So sánh mật khẩu gốc với mật khẩu mã hóa từ cơ sở dữ liệu
    public static boolean matches(String rawPassword, String encryptedPassword) {
        String hashedPassword = encrypt(rawPassword);
        return hashedPassword.equals(encryptedPassword);
    }

}
