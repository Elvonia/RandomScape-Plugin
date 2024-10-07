package com.kalico.randomscape;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import java.awt.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageUploadStyle;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomScapeOverlay extends Overlay
{

    private final Client client;
    private final RandomScapePlugin plugin;

    private Integer currentUnlock;
    private long displayTime;
    private int displayY;

    private final List<Integer> itemUnlockList;
    private boolean screenshotUnlock;
    private boolean includeFrame;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ImageCapture imageCapture;

    @Inject
    private ClientUI clientUi;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private DrawManager drawManager;

    @Inject
    public RandomScapeOverlay(Client client, RandomScapePlugin plugin)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.itemUnlockList = new ArrayList<>();
        this.screenshotUnlock = false;
        this.includeFrame = false;
        setPosition(OverlayPosition.TOP_CENTER);
    }

    public void addItemUnlock(int itemId)
    {
        itemUnlockList.add(itemId);
    }

    public void updateScreenshotUnlock(boolean doScreenshotUnlock, boolean doIncludeFrame)
    {
        screenshotUnlock = doScreenshotUnlock;
        includeFrame = doIncludeFrame;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN || itemUnlockList.isEmpty())
        {
            return null;
        }
        if (itemManager == null)
        {
            System.out.println("Item-manager is null");
            return null;
        }
        if (currentUnlock == null)
        {
            currentUnlock = itemUnlockList.get(0);
            displayTime = System.currentTimeMillis();
            displayY = -20;
            return null;
        }

        // Drawing unlock pop-up at the top of the screen.
        graphics.drawImage(plugin.getUnlockImage(),-62, displayY, null);
        graphics.drawImage(itemManager.getImage(currentUnlock, 1, false),-50, displayY + 7, null);
        if (displayY < 10)
        {
            displayY = displayY + 1;
        }

        if (System.currentTimeMillis() > displayTime + (5000))
        {
            if (screenshotUnlock)
            {
                int itemID = currentUnlock;
                ItemComposition itemComposition = itemManager.getItemComposition(itemID);
                String itemName = itemComposition.getName();
                String fileName = "ItemUnlocked " + itemName + " ";
                takeScreenshot(fileName);
            }
            itemUnlockList.remove(currentUnlock);
            currentUnlock = null;
        }
        return null;
    }

    /**
     * Saves a screenshot of the client window to the screenshot folder as a PNG,
     * and optionally uploads it to an image-hosting service.
     *
     * @param fileName    Filename to use, without file extension.
     */
    private void takeScreenshot(String fileName)
    {
        Consumer<Image> imageCallback = (img) ->
        {
            // This callback is on the game thread, move to executor thread
            executor.submit(() -> takeScreenshot(fileName, img));
        };

        drawManager.requestNextFrameListener(imageCallback);
    }

    private void takeScreenshot(String fileName, Image image)
    {
        BufferedImage screenshot = includeFrame
                ? new BufferedImage(clientUi.getWidth(), clientUi.getHeight(), BufferedImage.TYPE_INT_ARGB)
                : new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = screenshot.getGraphics();

        int gameOffsetX = 0;
        int gameOffsetY = 0;

        if (includeFrame)
        {
            // Draw the client frame onto the screenshot
            try
            {
                SwingUtilities.invokeAndWait(() -> clientUi.paint(graphics));
            }
            catch (InterruptedException | InvocationTargetException e)
            {
                log.warn("unable to paint client UI on screenshot", e);
            }

            // Evaluate the position of the game inside the frame
            final Point canvasOffset = clientUi.getCanvasOffset();
            gameOffsetX = (int)canvasOffset.getX();
            gameOffsetY = (int)canvasOffset.getY();
        }

        // Draw the game onto the screenshot
        graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
        imageCapture.takeScreenshot(screenshot, fileName, "Item Unlocks", false, ImageUploadStyle.NEITHER);
    }
}