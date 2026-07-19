package com.qr_vehicle.QRvehicle.util;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.ByteArrayOutputStream;

public class TagPdfGenerator {

    public static byte[] generateTag(byte[] qrBytes) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ✅ REAL STICKER SIZE (NO BLUR PRINT)
        float width = 100 * 2.83465f;   // 90mm
        float height = 60 * 2.83465f;  // 45mm

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, new PageSize(width, height));

        doc.setMargins(0, 0, 0, 0);

        // ✅ BACKGROUND TAG (FULL QUALITY)
        Image bg = new Image(
            ImageDataFactory.create(
                TagPdfGenerator.class.getResource("/static/tag.png")
            )
        );

        bg.scaleAbsolute(width, height);
        bg.setFixedPosition(0, 0);
        doc.add(bg);

        // ✅ QR IMAGE (NO DISTORTION)
        Image qr = new Image(ImageDataFactory.create(qrBytes));

        // 🔥 PERFECT ALIGNMENT (BASED ON YOUR DESIGN)
        float qrSize = 70; // slightly smaller for perfect padding

        // 🔥 PERFECT RIGHT ALIGNMENT
        float qrX = width - 82;   // move RIGHT (this fixes your issue)
        float qrY = height / 2 - 35; // vertical center

        qr.setFixedPosition(qrX, qrY);
        qr.scaleAbsolute(qrSize, qrSize);

        doc.add(qr);

        doc.close();

        return baos.toByteArray();
    }
}