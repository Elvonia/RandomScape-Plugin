package com.kalico;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomScapeOverlay extends Overlay
{

    private final Client client;
    private final RandomScapePlugin plugin;
    private int displayY;
    private int displayX;
    private long displayTime;
    private final List<ItemSlot> itemList;
    private boolean isResized;

    @Inject
    private ItemManager itemManager;

    @Inject
    public RandomScapeOverlay(Client client, RandomScapePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.itemList = new ArrayList<>();
        setPosition(OverlayPosition.TOP_CENTER);
    }

    public void addItemUnlock(List<Integer> itemIds)
    {
        isResized = client.isResized();
        for (int itemId : itemIds) {
            ItemSlot itemSlot = new ItemSlot(itemId, itemManager.getImage(itemId));
            itemList.add(itemSlot);

            log.debug("W: {} H: {}", itemSlot.image.getWidth(), itemSlot.image.getHeight());
        }
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN || itemManager == null)
        {
            return null;
        }
        if (itemList.isEmpty())
        {
            displayY = -20;
            displayTime = System.currentTimeMillis();
            return null;
        }
        if (isResized) // x-axis correction for fixed/resized modes
        {
            displayX = 13;
        } else {
            displayX = -93;
        }
        if (System.currentTimeMillis() > displayTime + (5000)) {
            graphics.drawImage(plugin.getUnlockImageText(), displayX, displayY, null);
        }
        else {
            graphics.drawImage(plugin.getUnlockImageNoText(), displayX, displayY, null);
        }

        // Overlay Dimensions:  180 x 65
        // Overlay Inner Dims:  176 x 36
        // Item Image Dims:     36 x 32

        for (ItemSlot item : itemList) {
            if (item.x == null) {
                item.x = itemList.indexOf(item) * 36 + displayX + 8;
            }
            if (item.isSpinning) {
                if (item.x >= displayX + 5) {
                    item.x -= 1;
                }
            }
            if (item.x > displayX + 5 - 36 && item.x < displayX + 5 + 175) {
                graphics.drawImage(item.getImage(), item.x, displayY + 7, null); // displayX + 78
            }
        }
        if (System.currentTimeMillis() > displayTime + (600)) {
            for (ItemSlot item : itemList) {
                if (item.x == null) {
                    continue;
                }
                item.isSpinning = System.currentTimeMillis() <= displayTime + (5000);
                if (!item.isSpinning) {
                    continue;
                }
                if (item.x < displayX + 5 && item.getImage().getWidth() == 1) {
                    itemList.add(new ItemSlot(item.itemId, item.image));
                    itemList.remove(item);
                    break;
                }
                if (item.x < displayX + 5 && item.getImage().getWidth() > 1) {
                    //log.debug("Item X: {}", displayX + 5 - item.x);
                    item.cropLeftImage(displayX + 5 - item.x);
                    //log.debug("CropL ID: {} W: {}", item.itemId, item.getImage().getWidth());
                }
                if (item.x < displayX + 5 + 176 && item.x > displayX + 5 + 140) {
                    //log.debug("Item X: {}", item.x - (displayX + 5 + 140));
                    item.cropRightImage(item.x - (displayX + 5 + 140));
                    //log.debug("CropR ID: {} W: {}", item.itemId, item.getImage().getWidth());
                }
            }
        }
        if (System.currentTimeMillis() > displayTime + (8000)) {
            itemList.clear();
        }
        // slides the overlay down vertically from above the screen
        if (displayY < 10)
        {
            displayY = displayY + 1;
        }
        return null;
    }
}