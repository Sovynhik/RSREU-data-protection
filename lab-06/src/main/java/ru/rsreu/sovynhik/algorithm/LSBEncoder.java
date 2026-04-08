package ru.rsreu.sovynhik.algorithm;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public class LSBEncoder {

    private int bits;

    public LSBEncoder(int bits) {
        this.bits = bits;
    }

    public void encode(BufferedImage image, String message) {
        try {
            byte[] data = (message + "#").getBytes(StandardCharsets.UTF_8);
            System.out.println("Сообщение в байтах (hex): " + bytesToHex(data));

            int width = image.getWidth();
            int height = image.getHeight();

            int bitIndex = 0;
            int byteIndex = 0;

            outer:
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (byteIndex >= data.length) break outer;

                    int rgb = image.getRGB(x, y);
                    int blue = rgb & 0xFF;

                    int bit = (data[byteIndex] >> (7 - bitIndex)) & 1;
                    blue = (blue & 0xFE) | bit;

                    rgb = (rgb & 0xFFFFFF00) | blue;
                    image.setRGB(x, y, rgb);

                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        byteIndex++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}