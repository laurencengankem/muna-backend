package com.example.kulvida.utils;

import com.example.kulvida.entity.UserOrder;
import com.example.kulvida.entity.cloth.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class PdfUtil {


    public void generateReceipt(String filename, List<UserOrder> order,String link) throws IOException {
        // Creating the Object of Document
        Document document = new Document(PageSize.A4);
        String orderId= order.get(0).getOrderId();

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        String dateFormatted = fmt.format(new Date());


        Image img = Image.getInstance("https://ik.imagekit.io/Heisen/logo_SWzBfmxa5.PNG?ik-sdk-version=javascript-1.4.3&updatedAt=1675951950433");
        img.scaleToFit(150,150);

        FileOutputStream fos= new FileOutputStream(filename);
        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, fos);

        // Opening the created document to modify it
        document.open();
        document.add(img);
        document.add(new Paragraph("\n\n"));
        //img.setAbsolutePosition(450f, 10f);
        // Creating font
        // Setting font style and size
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);

        // Creating paragraph
        Paragraph paragraph = new Paragraph("order n° "+orderId, fontTiltle);

        // Aligning the paragraph in document
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        // Adding the created paragraph in document
        document.add(paragraph);

        // Creating a table of 3 columns
        PdfPTable table = new PdfPTable(4);

        // Setting width of table, its columns and spacing
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{2, 2, 2,2});
        table.setSpacingBefore(10);

        // Create Table Cells for table header
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Setting the background color and padding
        cell.setBackgroundColor(CMYKColor.darkGray);
        cell.setPadding(5);

        // Creating font
        // Setting font style and size
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);



        // Adding headings in the created table cell/ header
        // Adding Cell to table
        cell.setPhrase(new Phrase("items", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("description", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("quantity", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("subtotal", font));
        table.addCell(cell);

        // Iterating over the list of userorders
        double total=0;
        cell.setBackgroundColor(Color.WHITE);
        for(UserOrder o : order) {
            cell.setPhrase(new Phrase(String.valueOf(o.getItem().getName())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getItem().getDescription())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getQuantity())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getSubTotal())));
            table.addCell(cell);

            total+= o.getSubTotal();
        }
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Delivery fees"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("0.0"));
        table.addCell(cell);
        cell.setBackgroundColor(CMYKColor.lightGray);
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Total"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell.setPhrase(new Phrase(String.valueOf(total)+"€"));
        table.addCell(cell);
        document.add(table);
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Order date: "+dateFormatted));
        document.add(new Paragraph("Track your order at the following link: "+link));



        // Closing the document
        document.close();
    }



    public void generateNewReceipt(String filename, List<OrderItem> order, String link) throws IOException {
        // Creating the Object of Document
        Document document = new Document(PageSize.A4);
        String orderId= order.get(0).getOrder().getOrderId();

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        String dateFormatted = fmt.format(new Date());


        Image img = Image.getInstance("https://ik.imagekit.io/Heisen/logo_SWzBfmxa5.PNG?ik-sdk-version=javascript-1.4.3&updatedAt=1675951950433");
        img.scaleToFit(150,150);

        FileOutputStream fos= new FileOutputStream(filename);
        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, fos);

        // Opening the created document to modify it
        document.open();
        document.add(img);
        document.add(new Paragraph("\n\n"));
        //img.setAbsolutePosition(450f, 10f);
        // Creating font
        // Setting font style and size
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);

        // Creating paragraph
        Paragraph paragraph = new Paragraph("order n° "+orderId, fontTiltle);

        // Aligning the paragraph in document
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        // Adding the created paragraph in document
        document.add(paragraph);

        // Creating a table of 3 columns
        PdfPTable table = new PdfPTable(4);

        // Setting width of table, its columns and spacing
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{2, 2, 2,2});
        table.setSpacingBefore(10);

        // Create Table Cells for table header
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Setting the background color and padding
        cell.setBackgroundColor(CMYKColor.darkGray);
        cell.setPadding(5);

        // Creating font
        // Setting font style and size
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);



        // Adding headings in the created table cell/ header
        // Adding Cell to table
        cell.setPhrase(new Phrase("items", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("size", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("description", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("quantity", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("subtotal", font));
        table.addCell(cell);

        // Iterating over the list of userorders
        double total=0;
        cell.setBackgroundColor(Color.WHITE);
        for(OrderItem o : order) {
            cell.setPhrase(new Phrase(String.valueOf(o.getCloth().getName())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getSize().getName())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getCloth().getDescription())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getQuantity())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getSubTotal())));
            table.addCell(cell);

            total+= o.getSubTotal();
        }
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Delivery fees"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("0.0"));
        table.addCell(cell);
        cell.setBackgroundColor(CMYKColor.lightGray);
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Total"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell.setPhrase(new Phrase(String.valueOf(total)+"€"));
        table.addCell(cell);
        document.add(table);
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Order date: "+dateFormatted));
        document.add(new Paragraph("Track your order at the following link: "+link));



        // Closing the document
        document.close();
    }
}
