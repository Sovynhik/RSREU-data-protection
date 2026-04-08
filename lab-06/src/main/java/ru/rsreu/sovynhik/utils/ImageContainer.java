package ru.rsreu.sovynhik.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageContainer {

    private final BufferedImage image;

    public ImageContainer(String path) throws IOException {
        image = ImageIO.read(new File(path));
    }

    public BufferedImage getImage() {
        return image;
    }

    public void save(String path) throws IOException {
        ImageIO.write(image, "bmp", new File(path));
    }
}