package ru.ereke.appsalem;

import java.io.File;

/**
 * Created by Erasyl on 07.02.2017.
 */

public class FileManager {
    // Сделаем путь для фото
    public static File getFile() {
        File foloder = new File("sdcard/camera_app"); // местоположения
        if (!foloder.exists()) {
            foloder.mkdir();
        }
        File image_file = new File(foloder, "cam_image.jpg"); // имя фото
        return image_file;
    }
}
