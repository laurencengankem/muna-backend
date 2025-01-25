package com.example.kulvida.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ProductCodeGenerator {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";  // Use AES with padding
    private static final String SECRET_KEY = "MySecretKey123456";    // 16 bytes key
    private static final int FIXED_LENGTH = 12;  // Desired fixed length for product code

    public static void main(String[] args) throws Exception {
        String productId = "123";   // Example product ID (digit)
        String size = "L";          // Example size

        // Generate the fixed-length product code
        String productCode = generateProductCode(productId, size);
        System.out.println("Encrypted Product Code: " + productCode);

        // Decrypt the product code
        String decryptedData = decryptProductCode(productCode);
        System.out.println("Decrypted Data: " + decryptedData);
    }

    public static String generateProductCode(String productId, String size) throws Exception {
        // Concatenate product ID and size
        String combinedString = productId + ":" + size;

        // Encrypt the combined string and make sure the result is of fixed length
        String encrypted = encrypt(combinedString);
        return ensureFixedLength(encrypted, FIXED_LENGTH);
    }

    private static String encrypt(String data) throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        // Encode the encrypted bytes to a Base64 string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptProductCode(String productCode) throws Exception {
        // Decode the Base64 string back to bytes
        byte[] encryptedBytes = Base64.getDecoder().decode(productCode);

        byte[] keyBytes = SECRET_KEY.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convert decrypted bytes back to a string (product ID: size)
        return new String(decryptedBytes);
    }

    // Ensure the encrypted code has a fixed length (pad or truncate as needed)
    private static String ensureFixedLength(String encrypted, int length) {
        if (encrypted.length() < length) {
            // Pad with 'X' if too short
            return String.format("%1$-" + length + "s", encrypted).replace(' ', 'X');
        } else if (encrypted.length() > length) {
            // Truncate if too long
            return encrypted.substring(0, length);
        }
        return encrypted;  // Already the correct length
    }
}
