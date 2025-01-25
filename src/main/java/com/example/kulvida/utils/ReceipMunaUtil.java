package com.example.kulvida.utils;

import com.example.kulvida.entity.UserItem;
import com.example.kulvida.entity.cloth.OrderItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class ReceipMunaUtil {

    public static String generateReceipt(List<OrderItem> items) {
        // Define receipt content
        String storeName = "Muna";
        String address = "Douala Bonandjo";
        String phone = "Tel: (+237) 677876534";
        String cashier = "Cashier: Tat CE1";
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(items.get(0).getOrder().getOrderDate().getTime());

        StringBuilder receipt = new StringBuilder();

        receipt.append("-------------------------------------------\n");
        receipt.append(date).append("\n");
        receipt.append(cashier).append("\n");
        receipt.append("-------------------------------------------\n");
        receipt.append(String.format("%-15s%10s%6s%12s%n", "Item", "Size", "Qty", "Price"));
        Double total=0.0;
        for(OrderItem item: items){
            String name= item.getCloth().getName().length()>10? item.getCloth().getName().substring(0,10).trim()+"...": item.getCloth().getName();
            receipt.append(String.format("%-15s%10s%6d%12.2f%n", name, item.getSize().getName(), item.getQuantity(), item.getSubTotal()));
            total+=item.getSubTotal();
        }
        receipt.append("-------------------------------------------\n");
        receipt.append(String.format("%-25s%10.2f %s%n", "Total:", total, "XAF"));
        receipt.append("\n          Order N° "+items.get(0).getOrder().getOrderId()+"\n\n");
        receipt.append("\n          Thank you for shopping!\n\n");


        int height= 400+ 1100;

        return createReceiptImage(receipt.toString(), height,"receipt_with_base64_image.png");
    }


    public static String createReceiptImage(String receiptText,int height, String outputFileName) {

        BufferedImage headerImage = null;
        try {
            byte[] imageBytes = Base64.getDecoder().decode("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");
            headerImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error decoding header image from base64.");
        }

        // Image dimensions
        int width = 320;
        int headerHeight = (headerImage != null) ? headerImage.getHeight() : 0;

        // Calculate dynamic height based on receipt text
        int lineHeight = 15;   // Height of each line of text
        int padding = 20;      // Padding below header image
        int calculatedHeight = (receiptText.split("\n").length * lineHeight) + padding + headerHeight;

        BufferedImage receiptImage = new BufferedImage(width, calculatedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = receiptImage.createGraphics();

        // Set background color
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, calculatedHeight);

        // Draw header image if available
        int y = 0;  // Vertical position tracker
        if (headerImage != null) {
            g.drawImage(headerImage, (width - headerImage.getWidth()) / 2, y, null); // Center image
            y += headerHeight + padding;
        }

        // Set font and color for text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Draw receipt text line by line
        for (String line : receiptText.split("\n")) {
            g.drawString(line, 10, y);
            y += lineHeight;  // Move to next line
        }

        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(receiptImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error converting receipt image to base64.");
        }
        return null;
    }


}


