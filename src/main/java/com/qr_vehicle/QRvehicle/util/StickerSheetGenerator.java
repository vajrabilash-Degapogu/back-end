package com.qr_vehicle.QRvehicle.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.qr_vehicle.QRvehicle.entity.TagInventory;

public class StickerSheetGenerator {

    private static float cm(float cm) {
        return cm * 28.3465f;
    }

    public static byte[] generate(List<TagInventory> inventoryList) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);

        PdfDocument pdf = new PdfDocument(writer);

        pdf.setDefaultPageSize(new PageSize(cm(40), cm(30)));

        pdf.addNewPage();

        PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());

        float stickerWidth = cm(10);
        float stickerHeight = cm(6);

        for (TagInventory inventory : inventoryList) {

            int row = inventory.getSheetRow() - 1;

            int firstColumn = inventory.getSheetColumn() - 1;

            for (int copy = 0; copy < 2; copy++) {

                int currentColumn = firstColumn + copy;

                float x = currentColumn * stickerWidth;

                float y = cm(30) - ((row + 1) * stickerHeight);

                Rectangle area = new Rectangle(
                        x,
                        y,
                        stickerWidth,
                        stickerHeight);

                StickerPdfRenderer.drawSticker(
                        canvas,
                        area,
                        inventory.getUniqueCode(),
                        inventory.getTagId(),
                        inventory.getSheetCode());
            }
        }

        pdf.close();

        
        return baos.toByteArray();
    }
}