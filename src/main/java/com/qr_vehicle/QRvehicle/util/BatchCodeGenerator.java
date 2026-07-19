package com.qr_vehicle.QRvehicle.util;

public class BatchCodeGenerator {

    public static String generate(String type, long count) {

        if ("REVERSE".equals(type)) {
            return String.format("REV%05d", count + 1);
        }

        return String.format("NOR%05d", count + 1);

    }

}