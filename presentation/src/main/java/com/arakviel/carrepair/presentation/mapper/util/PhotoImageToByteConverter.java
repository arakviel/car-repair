package com.arakviel.carrepair.presentation.mapper.util;

import io.github.palexdev.materialfx.utils.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public final class PhotoImageToByteConverter {

    public static byte[] convert(Image image) {
        if (Objects.isNull(image)) return null;
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        if (Objects.isNull(bufferedImage)) return null;
        try (var stream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", stream);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PhotoImageToByteConverter() {}
}
