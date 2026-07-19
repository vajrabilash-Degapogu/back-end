package com.qr_vehicle.QRvehicle.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import java.awt.FontMetrics;


public class StickerImageGenerator {

    public static BufferedImage generate(
        String uniqueCode,
        String tagId,
        String batchCode,String sheetCode) throws Exception {

    ClassPathResource resource =
            new ClassPathResource("static/tag.png");

    BufferedImage template =
            ImageIO.read(resource.getInputStream());

//     String qrUrl = "http://localhost:3000/vehicle/" + uniqueCode;
        String qrUrl = "https://owntag.in/vehicle/" + uniqueCode;

        BufferedImage qr =
        QRCodeGenerator.generate(
                qrUrl,
                StickerLayout.QR_SIZE);

    Graphics2D g = template.createGraphics();

    g.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);

    g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

    g.drawImage(
            qr,
            StickerLayout.QR_X,
            StickerLayout.QR_Y,
            StickerLayout.QR_SIZE,
            StickerLayout.QR_SIZE,
            null);

    // ---------- Tag ID ----------
    g.setColor(java.awt.Color.BLACK);

    Font tagFont = new Font("Arial", Font.BOLD, 30);

    g.setFont(tagFont);

    FontMetrics fm = g.getFontMetrics();

    int textWidth = fm.stringWidth(tagId);

    int tagX =
            StickerLayout.QR_X +
            (StickerLayout.QR_SIZE - textWidth) / 2;

    g.drawString(
            tagId,
            tagX,
            StickerLayout.TAG_ID_Y);

    // ---------- Batch ----------
    Font batchFont = new Font("Arial", Font.PLAIN, 18);

    g.setFont(batchFont);

    g.drawString(
        "Sheet : " + sheetCode,
            StickerLayout.BATCH_X,
            StickerLayout.BATCH_Y+22);

    g.dispose();

    return template;
}

}