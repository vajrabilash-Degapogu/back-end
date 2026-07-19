package com.qr_vehicle.QRvehicle.util;

import java.security.SecureRandom;

public class RandomTagGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {

        return String.valueOf(
                10000000 + random.nextInt(90000000)
        );

    }

}