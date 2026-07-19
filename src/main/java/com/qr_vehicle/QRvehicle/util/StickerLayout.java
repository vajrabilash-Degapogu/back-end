package com.qr_vehicle.QRvehicle.util;

public class StickerLayout {

    private StickerLayout() {
    }

   // Sticker Size
    public static final float STICKER_WIDTH_CM = 10f;
    public static final float STICKER_HEIGHT_CM = 6f;

    // Sheet
    public static final int ROWS = 5;
    public static final int COLUMNS = 4;

    // PDF Page
    public static final float PAGE_WIDTH_CM = 40f;
    public static final float PAGE_HEIGHT_CM = 30f;

    // QR Position
    public static final int QR_X = 780;      // Move 8-10 px right
    public static final int QR_Y = 190;      // Move 8-10 px down
    public static final int QR_SIZE = 345;   // Increase from ~295 to 320

    

      

    // Tag ID Position (centered under QR)
   // Tag ID
    public static final int TAG_ID_X = 830;
    public static final int TAG_ID_Y = 650;  // Move below "owntag.in"

// Batch Code (optional)
    public static final int BATCH_X = 40;
    public static final int BATCH_Y = 690;

}