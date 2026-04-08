package ru.rsreu.sovynhik.algorithm;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class LSBDecoder {

    private int bits;

    public LSBDecoder(int bits) {
        this.bits = bits;
    }

    public String decode(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        StringBuilder allBits = new StringBuilder(width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int blue = rgb & 0xFF;
                int lsb = blue & 1;
                allBits.append(lsb);
            }
        }

        String bits = allBits.toString();
        System.out.println("Извлечено бит всего: " + bits.length());

        // Берём только полные байты
        int byteCount = bits.length() / 8;
        byte[] extracted = new byte[byteCount];

        for (int i = 0; i < byteCount; i++) {
            String eight = bits.substring(i * 8, i * 8 + 8);
            extracted[i] = (byte) Integer.parseInt(eight, 2);
        }

        System.out.println("Извлечено байт (hex): " + bytesToHex(extracted));

        String result;
        try {
            result = new String(extracted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("UTF-8 не сработал, пробуем windows-1251");
            try {
                result = new String(extracted, "windows-1251");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }

        int end = result.indexOf("#");
        if (end >= 0) {
            result = result.substring(0, end);
        }

        return result.trim();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}