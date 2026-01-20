package com.example.kulvida.controller;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class Tester {

    public static void main(String[] args) {
        String imageUrl = "https://ik.imagekit.io/Heisen/pant_seFDrTTab.jpg"; // Replace with actual image URL
        String saveDir = "C:/Users/laure/Desktop/photos"; // Replace with desired save directory
        String[] parts= imageUrl.split("/");
        String fileName = imageUrl.split("/")[parts.length-1];

        try {
            downloadImage(imageUrl, saveDir, fileName);
            System.out.println("Download complete: " + saveDir + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadImage(String imageUrl, String saveDir, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        Path filePath = Paths.get(saveDir, fileName);
        Files.createDirectories(Paths.get(saveDir));

        try (InputStream in = url.openStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
