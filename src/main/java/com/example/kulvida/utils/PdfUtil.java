package com.example.kulvida.utils;

import com.example.kulvida.entity.UserOrder;
import com.example.kulvida.entity.cloth.OrderItem;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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
        cell.setPhrase(new Phrase("0.00"));
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

    public byte[] generatePOSReceipt(List<OrderItem> order){

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.kernel.pdf.PdfPage page = pdfDoc.addNewPage(new com.itextpdf.kernel.geom.PageSize(200, 400 + (20*order.size())));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);


            com.itextpdf.kernel.font.PdfFont font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.COURIER);
            document.setFont(font);

            document.setFontSize(8.0f);

            // Header
            String storeName = "Muna";
            String address = "Douala Bonandjo";
            String phone = "Tel: (+237) 677876534";
            String cashier = "Caissier: "+order.get(0).getOrder().getUser().getFullName();
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(order.get(0).getOrder().getOrderDate().getTime());
            DecimalFormat nf = new DecimalFormat("#,##0.00");

            document.add(new com.itextpdf.layout.element.Paragraph("MY STORE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(14));

            document.add(new com.itextpdf.layout.element.Paragraph("--------------------------------------------------"));
            document.add(new com.itextpdf.layout.element.Paragraph(date+"\n"+cashier)
                    .setTextAlignment(TextAlignment.LEFT));

            document.add(new com.itextpdf.layout.element.Paragraph("--------------------------------------------------"));
            document.add(new com.itextpdf.layout.element.Paragraph("Article   Code   Qté   Prix")
                    .setFont(font) // Apply font
                    .setWidth(UnitValue.createPercentValue(100))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(8.0f));

            double total = 0;

            for (OrderItem o : order) {
                document.add(new com.itextpdf.layout.element.Paragraph(
                        String.format("%-10s %-10s %-5s %10s",
                                o.getCloth().getName().toLowerCase(Locale.ROOT),
                                o.getCloth().getCode() + o.getSize().getName(),
                                String.valueOf(o.getQuantity()),
                                nf.format(o.getPrice()))
                )
                        .setWidth(UnitValue.createPercentValue(100))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(7.0f)
                        .setFont(com.itextpdf.io.font.constants.StandardFonts.COURIER));

                total += o.getSubTotal();
            }
            document.add(new com.itextpdf.layout.element.Paragraph("--------------------------------------------------"));
            document.add(new com.itextpdf.layout.element.Paragraph("Total: "+nf.format(total)+" XAF")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold());
            document.add(new com.itextpdf.layout.element.Paragraph("TVA(5%): "+nf.format(total*5/100) +" XAF")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold());


            document.add(new com.itextpdf.layout.element.Paragraph("\nMerci Pour L'achat!\nà très bientot!")
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public byte[] generateNewReceipt(String filename, List<OrderItem> order) throws IOException {
        // Creating the Object of Document
        Document document = new Document(PageSize.A4);
        String orderId= order.get(0).getOrder().getOrderId();

        DecimalFormat nf = new DecimalFormat("#,##0.00");

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = fmt.format(order.get(0).getOrder().getOrderDate().getTime());
        byte[] imageBytes = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD/4gHYSUNDX1BST0ZJTEUAAQEAAAHIAAAAAAQwAABtbnRyUkdCIFhZWiAH4AABAAEAAAAAAABhY3NwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAA9tYAAQAAAADTLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAlkZXNjAAAA8AAAACRyWFlaAAABFAAAABRnWFlaAAABKAAAABRiWFlaAAABPAAAABR3dHB0AAABUAAAABRyVFJDAAABZAAAAChnVFJDAAABZAAAAChiVFJDAAABZAAAAChjcHJ0AAABjAAAADxtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAAgAAAAcAHMAUgBHAEJYWVogAAAAAAAAb6IAADj1AAADkFhZWiAAAAAAAABimQAAt4UAABjaWFlaIAAAAAAAACSgAAAPhAAAts9YWVogAAAAAAAA9tYAAQAAAADTLXBhcmEAAAAAAAQAAAACZmYAAPKnAAANWQAAE9AAAApbAAAAAAAAAABtbHVjAAAAAAAAAAEAAAAMZW5VUwAAACAAAAAcAEcAbwBvAGcAbABlACAASQBuAGMALgAgADIAMAAxADb/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAG0BkADASIAAhEBAxEB/8QAHgABAAIDAQADAQAAAAAAAAAAAAgJBgcKBQEDBAL/xABgEAABAwIDBAILCgkJBgQDCQEAAQIDBAUGBxEICRIhGTETIjc4QVd1lrPS1BRRVlhhcXaVsrQVFyMyUpGTlNMYNkJUdIGStcMWJDM1c6FDcqKxRGKCJSYnNFNjhcHE0f/EABQBAQAAAAAAAAAAAAAAAAAAAAD/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCz0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA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");

        // Create an Image instance from the byte array
        Image img = Image.getInstance(imageBytes);
        img.scaleToFit(300, 250);
        img.setAlignment(Element.ALIGN_CENTER);


        //FileOutputStream fos= new FileOutputStream(filename);
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
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
        Paragraph paragraph = new Paragraph("Commande N° "+orderId, fontTiltle);

        // Aligning the paragraph in document
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        // Adding the created paragraph in document
        document.add(paragraph);

        // Creating a table of 3 columns
        PdfPTable table = new PdfPTable(5);

        // Setting width of table, its columns and spacing
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{2, 2, 2,2,2});
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
        cell.setPhrase(new Phrase("Code", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Article", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Quantité", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Prix", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Total", font));
        table.addCell(cell);

        // Iterating over the list of userorders
        double total=0;
        cell.setBackgroundColor(Color.WHITE);
        font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12);
        for(OrderItem o : order) {
            cell.setPhrase(new Phrase(String.valueOf(o.getCloth().getCode()+o.getSize().getName()),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getCloth().getName()),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(o.getQuantity()),font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(nf.format(o.getPrice())+" XAF",font));
            table.addCell(cell);

            cell.setPhrase(new Phrase(nf.format(o.getSubTotal())+" XAF",font));
            table.addCell(cell);

            total+= o.getSubTotal();
        }
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Frais De Livraison"));
        table.addCell(cell);
        cell.setColspan(2);
        cell.setPhrase(new Phrase("0,00 XAF"));
        table.addCell(cell);
        cell.setColspan(3);
        cell.setPhrase(new Phrase("TVA (5%)"));
        table.addCell(cell);
        cell.setColspan(2);
        cell.setPhrase(new Phrase(nf.format(total*5/100) +" XAF"));
        table.addCell(cell);
        cell.setBackgroundColor(CMYKColor.lightGray);
        cell.setColspan(3);
        cell.setPhrase(new Phrase("Total"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell.setColspan(2);
        cell.setPhrase(new Phrase(nf.format(total)+" XAF"));
        table.addCell(cell);
        document.add(table);
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Date Commande: "+dateFormatted));
        //document.add(new Paragraph("Track your order at the following link: "+link));



        // Closing the document
        document.close();
        return fos.toByteArray();
    }


}
