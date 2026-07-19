package com.qr_vehicle.QRvehicle.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.qr_vehicle.QRvehicle.entity.TagInventory;

public class StickerSheetGenerator {

    private static float cm(float cm) {
        return cm * 28.3465f;
    }

    public static byte[] generate(List<TagInventory> inventoryList) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);

        PdfDocument pdf = new PdfDocument(writer);

        pdf.setDefaultPageSize(
                new PageSize(
                        cm(40),
                        cm(30)));

        Document document = new Document(pdf);

        float gap = cm(0.2f);

        float stickerWidth = cm(10) - gap;
        float stickerHeight = cm(6) - gap;

        for (TagInventory inventory : inventoryList) {

    BufferedImage sticker =
            StickerImageGenerator.generate(
        inventory.getUniqueCode(),
        inventory.getTagId(),
        inventory.getBatchCode(),
        inventory.getSheetCode());

    ByteArrayOutputStream imageOut =
            new ByteArrayOutputStream();

    ImageIO.write(sticker, "png", imageOut);

    byte[] imageBytes = imageOut.toByteArray();

    int row = inventory.getSheetRow() - 1;

    int firstColumn = inventory.getSheetColumn() - 1;

    for (int copy = 0; copy < 2; copy++) {

        int currentColumn = firstColumn + copy;

        Image img = new Image(
                ImageDataFactory.create(imageBytes));

        img.scaleAbsolute(
                stickerWidth,
                stickerHeight);

        float x = currentColumn * cm(10);


        float y = cm(30) - ((row + 1) * cm(6));

        img.setFixedPosition(x, y);

        document.add(img);
    }

}

        document.close();

        return baos.toByteArray();

    }

}