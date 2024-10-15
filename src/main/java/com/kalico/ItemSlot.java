package com.kalico;

import java.awt.image.BufferedImage;

public class ItemSlot {
    Integer itemId;
    Integer x;
    BufferedImage image;
    BufferedImage croppedImage;
    boolean isSpinning;

    public ItemSlot(Integer itemId, BufferedImage image) {
        this.itemId = itemId;
        this.image = image;
    }

    public BufferedImage getImage() {
        if (croppedImage != null) {
            return croppedImage;
        }
        return image;
    }

    public void cropLeftImage(int offset) {
        croppedImage = getImage();
        croppedImage = croppedImage.getSubimage(offset, 0, croppedImage.getWidth() - offset, croppedImage.getHeight());
    }

    public void cropRightImage(int offset) {
        croppedImage = image;
        croppedImage = croppedImage.getSubimage(0, 0, 36 - offset, croppedImage.getHeight());
    }
}