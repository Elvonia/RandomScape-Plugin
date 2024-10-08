package com.kalico.randomscape;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.Runnables;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.events.PluginChanged;
import net.runelite.api.ChatMessageType;
import net.runelite.api.widgets.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.Text;
import net.runelite.client.game.WorldService;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
		name = "RandomScape",
		description = "Unlocks items randomly as you gain new unique drops/pickups",
		tags = {"overlay", "random", "randomscape", "custom", "mode", "gamble"}
)
public class RandomScapePlugin extends Plugin
{
	static final String CONFIG_GROUP = "randomscape";
	public static final String CONFIG_KEY = "unlockeditems";
	private static final String BM_UNLOCKS_STRING = "!rsunlocks";
	private static final String BM_COUNT_STRING = "!rscount";
	private static final String BM_RESET_STRING = "!rsreset";
	private static final String BM_BACKUP_STRING = "!rsbackup";

	final int COMBAT_ACHIEVEMENT_BUTTON = 20;
	final int COLLECTION_LOG_GROUP_ID = 621;
	final int COLLECTION_VIEW = 36;
	final int COLLECTION_VIEW_SCROLLBAR = 37;
	final int COLLECTION_VIEW_HEADER = 19;

	final int INVENTORY_GROUP_ID = 149;
	final int INVENTORY_CHILD_ID = 0;
	final int SHOP_INVENTORY_GROUP_ID = 301;
	final int SHOP_INVENTORY_CHILD_ID = 0;
	final int SHOP_GROUP_ID = 300;
	final int SHOP_CHILD_ID = 16;
	final int BANK_ITEM_CONTAINER_GROUP_ID = 12;
	final int BANK_ITEM_CONTAINER_CHILD_ID = 13;
	final int MAGIC_SPELL_PREVIEW_GROUP_ID = 218;
	final int MAGIC_SPELL_PREVIEW_CHILD_ID = 202;
	final int INVENTORY_BANK_GROUP_ID = 15;
	final int INVENTORY_BANK_CHILD_ID = 3;

	final int INVENTORY_DRAW_ITEM = 6011;
	final int INVENTORY_DRAG_RELEASE_ITEM = 6013;
	final int INVENTORY_ITEM_MENU = 488;
	final int INVENTORY_ITEM_MENU_CLICK = 6014;
	final int INVENTORY_DRAG_ITEM = 6063;
	final int INVENTORY_SHOP_INIT_ITEMS = 151;

	final int MAGIC_CAST_CLICK_SPELL = 4902;
	final int MAGIC_CLICK_SPELL = 2617;
	final int MAGIC_HOVER_SPELL = 2622;
	final int MAGIC_SPELLBOOK_REDRAW_CS = 2610;
	final int MAGIC_SPELLBOOK_REDRAW_PROC = 2611;
	final int MAGIC_SPELLBOOK_INIT = 2622;
	final int MAGIC_SPELLBOOK_INIT_SPELLS = 2616;
	final int MAGIC_SPELLBOOK_HASRUNES = 2620;

	final int INITIATE_SHOP_INTERFACE = 1074;
	final int BANK_CONTAINER = 839;

	final int COLLECTION_VIEW_CATEGORIES_CONTAINER = 28;
	final int COLLECTION_VIEW_CATEGORIES_RECTANGLE = 33;
	final int COLLECTION_VIEW_CATEGORIES_TEXT = 34;
	final int COLLECTION_VIEW_CATEGORIES_SCROLLBAR = 28;

	final int MENU_INSPECT = 2;
	final int MENU_DELETE = 3;

	final int SELECTED_OPACITY = 200;
	final int UNSELECTED_OPACITY = 235;

	private static final int GE_SEARCH_RESULTS = 50;
	private static final int GE_SEARCH_BUILD_SCRIPT = 751;

	private static final int COLLECTION_LOG_OPEN_OTHER = 2728;
	private static final int COLLECTION_LOG_DRAW_LIST = 2730;
	private static final int COLLECTION_LOG_ITEM_CLICK = 2733;

	static final Set<Integer> OWNED_INVENTORY_IDS = ImmutableSet.of(
			0,    // Reward from fishing trawler.
			93,   // Standard player inventory.
			94,   // Equipment inventory.
			95,   // Bank inventory.
			141,  // Barrows reward chest inventory.
			390,  // Kingdom Of Miscellania reward inventory.
			581,  // Chambers of Xeric chest inventory.
			612,  // Theater of Blood reward chest inventory (Raids 2).
			626); // Seed vault located inside the Farming Guild.

	static final List<int[]> EXCLUDED_ITEM_IDS = Arrays.asList(
			new int[]{25990, 26153},
			new int[]{26421, 26560},
			new int[]{24361, 24475},
			new int[]{25001, 25117},
			new int[]{25359, 25388}
	);

	@Inject
	private Client client;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Notifier notifier;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldService worldService;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ChatCommandManager chatCommandManager;

	@Inject
	private RandomScapeConfig config;

	@Inject
	private RandomScapeOverlay randomScapeOverlay;

	private SpellSpriteMap spellSpriteMap = new SpellSpriteMap();

	private Map<Integer, Integer> unlockedItems;

	@Getter
	private BufferedImage unlockImage = null;

	private static final String SCRIPT_EVENT_SET_CHATBOX_INPUT = "setChatboxInput";

	private ChatboxTextInput searchInput;
	private Widget searchButton;
	private Widget swapButton;
	private Collection<Widget> itemEntries;

	private List<String> namesRandomScape = new ArrayList<>();
	private int randomScapeIconOffset = -1;
	private boolean onSeasonalWorld;
	private boolean deleteConfirmed = false;
	private File legacyFile;
	private File legacyFolder;
	private File profileFile;
	private File profileFolder;
	private String profileKey;

	private List<Integer> cachedTradableItems;
	private boolean swapView = false;
	private Widget hoveredSpellWidget;

	@Provides
	RandomScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RandomScapeConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		super.startUp();
		onSeasonalWorld = false;
		updateNamesRandomScape();
		updateScreenshotUnlock();
		loadResources();
		unlockedItems = new HashMap<>();
		overlayManager.add(randomScapeOverlay);
		spellSpriteMap.initialize();
		chatCommandManager.registerCommand(BM_UNLOCKS_STRING, this::OnUnlocksCountCommand);
		chatCommandManager.registerCommand(BM_COUNT_STRING, this::OnUnlocksCountCommand);
		chatCommandManager.registerCommand(BM_BACKUP_STRING, this::OnUnlocksBackupCommand);

		if (config.resetCommand())
		{
			chatCommandManager.registerCommand(BM_RESET_STRING, this::OnUnlocksResetCommand);
		}

		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				onSeasonalWorld = isSeasonalWorld(client.getWorld());
				if (!onSeasonalWorld)
				{
					setChatboxName(getNameChatbox());
				}
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		super.shutDown();
		itemEntries = null;
		unlockedItems = null;
		overlayManager.remove(randomScapeOverlay);
		spellSpriteMap.shutdown();
		chatCommandManager.unregisterCommand(BM_UNLOCKS_STRING);
		chatCommandManager.unregisterCommand(BM_COUNT_STRING);
		chatCommandManager.unregisterCommand(BM_BACKUP_STRING);
		if (config.resetCommand())
		{
			chatCommandManager.unregisterCommand(BM_RESET_STRING);
		}

		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN && !onSeasonalWorld)
			{
				setChatboxName(getNameDefault());
			}
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() == GameState.LOGGED_IN)
		{
			setupUnlockHistory();
			loadPlayerUnlocks();
			loadResources();
			cachedTradableItems = getAllTradableItems();
			onSeasonalWorld = isSeasonalWorld(client.getWorld());
		}
		if (e.getGameState() == GameState.LOGIN_SCREEN)
		{
			itemEntries = null;
		}
	}

	@Subscribe
	public void onPluginChanged(PluginChanged e)
	{
		if (e.getPlugin() == this && client.getGameState() == GameState.LOGGED_IN)
		{
			setupUnlockHistory();
			loadPlayerUnlocks();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		if (e.getGroupId() != COLLECTION_LOG_GROUP_ID || config.moveCollectionLogUnlocks()) {
			return;
		}

		Widget collectionViewHeader = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_HEADER);
		openRandomScapeCategory(collectionViewHeader);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged e)
	{
		if (OWNED_INVENTORY_IDS.contains(e.getContainerId()))
		{
			unlockItemContainerItems(e.getItemContainer());
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event) {
		if (event.getScriptId() == MAGIC_HOVER_SPELL) {
			ScriptEvent scriptEvent = event.getScriptEvent();
			Object[] scriptArgs = scriptEvent.getArguments();

			if (scriptArgs == null) {
				return;
			}

			hoveredSpellWidget = scriptEvent.getSource();
		}

		if (event.getScriptId() == MAGIC_SPELLBOOK_HASRUNES ||
				event.getScriptId() == MAGIC_SPELLBOOK_REDRAW_CS ||
				event.getScriptId() == MAGIC_SPELLBOOK_REDRAW_PROC ||
				event.getScriptId() == MAGIC_SPELLBOOK_INIT ||
				event.getScriptId() == MAGIC_SPELLBOOK_INIT_SPELLS) {
			log.debug("Script ID: " + event.getScriptId() + " Script Event Name: " + event.getScriptEvent());
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event) {
		if (event.getScriptId() == GE_SEARCH_BUILD_SCRIPT) {
			killSearchResults();
		}

		if (event.getScriptId() == MAGIC_CLICK_SPELL || event.getScriptId() == MAGIC_HOVER_SPELL) {
			updateSpellbookItemChecks(hoveredSpellWidget);
		}

		if ((event.getScriptId() == COLLECTION_LOG_DRAW_LIST) && !config.moveCollectionLogUnlocks()) {
			Widget collectionLogWidget = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_HEADER);
			openRandomScapeCategory(collectionLogWidget);
		}

		if (event.getScriptId() == INVENTORY_DRAW_ITEM || event.getScriptId() == INVENTORY_ITEM_MENU ||
				event.getScriptId() == INVENTORY_SHOP_INIT_ITEMS) {
			updateIconOpacity(INVENTORY_GROUP_ID, INVENTORY_CHILD_ID);
			updateIconOpacity(SHOP_INVENTORY_GROUP_ID, SHOP_INVENTORY_CHILD_ID);
			updateIconOpacity(SHOP_GROUP_ID, SHOP_CHILD_ID);
			updateIconOpacity(BANK_ITEM_CONTAINER_GROUP_ID, BANK_ITEM_CONTAINER_CHILD_ID);
		}

		if (event.getScriptId() == INITIATE_SHOP_INTERFACE) {
			updateIconOpacity(SHOP_GROUP_ID, SHOP_CHILD_ID);
		}

		if (event.getScriptId() == BANK_CONTAINER) {
			updateIconOpacity(BANK_ITEM_CONTAINER_GROUP_ID, BANK_ITEM_CONTAINER_CHILD_ID);
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		//log.debug("Script: " + scriptCallbackEvent.getScript() + " Name: " + scriptCallbackEvent.getEventName());
		if (scriptCallbackEvent.getEventName().equals(SCRIPT_EVENT_SET_CHATBOX_INPUT) && !onSeasonalWorld)
		{
			setChatboxName(getNameChatbox());
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		String name = Text.removeTags(chatMessage.getName());
		switch (chatMessage.getType())
		{
			case PRIVATECHAT:
			case MODPRIVATECHAT:
				// Note this is unable to change icon on PMs if they are not a friend or in friends chat
			case CLAN_CHAT:
			case CLAN_GUEST_CHAT:
			case FRIENDSCHAT:
				if (isChatPlayerOnNormalWorld(name) && isChatPlayerRandomScaper(name))
				{
					addRandomScapeIconToMessage(chatMessage);
				}
				break;
			case PUBLICCHAT:
			case MODCHAT:
				if (!onSeasonalWorld && isChatPlayerRandomScaper(name))
				{
					addRandomScapeIconToMessage(chatMessage);
				}
				break;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			if (event.getKey().equals("namesRandomScapers"))
			{
				updateNamesRandomScape();
			}
			else if (event.getKey().equals("screenshotUnlock") || event.getKey().equals("includeFrame"))
			{
				updateScreenshotUnlock();
			}
			else if (event.getKey().equals("resetCommand"))
			{
				if (config.resetCommand())
				{
					chatCommandManager.registerCommand(BM_RESET_STRING, this::OnUnlocksResetCommand);
				}
				else
				{
					chatCommandManager.unregisterCommand(BM_RESET_STRING);
				}
			}
		}
	}

	@Subscribe
	public void onLootReceived(LootReceived event) {
		Collection<ItemStack> items = event.getItems();
		for (ItemStack itemStack : items) {
			final int itemId = itemStack.getId();
			final int realItemId = itemManager.canonicalize(itemId);
			final boolean isTradeable = itemManager.getItemComposition(realItemId).isTradeable();

			if (itemId == -1 || unlockedItems.containsKey(realItemId)) {
				return;
			}

			if (!isTradeable && !unlockedItems.containsKey(realItemId)) {
				queueItemUnlock(realItemId, realItemId);
				notifyPlayerOfUnlock(realItemId);
			}

			int randomItemId = getRandomTradableItem(cachedTradableItems);
				queueItemUnlock(realItemId, randomItemId);
				notifyPlayerOfUnlock(randomItemId);
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (!isInventoryItemMenu(event) && !isShopItemMenu(event) &&
				!isShopInventoryMenu(event)) {
			return;
		}

		final int itemId = event.getItemId();
		final boolean isTradeable = itemManager.getItemComposition(itemId).isTradeable();

		if (!unlockedItems.containsValue(itemId) && isTradeable) {
			modifyMenuEntries(itemId);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuOption().equals("Take")) {
			final int itemId = event.getId();
			final boolean isTradeable = itemManager.getItemComposition(itemId).isTradeable();
			final String itemName = itemManager.getItemComposition(itemId).getName();

			if (!unlockedItems.containsValue(itemId) && isTradeable) {
				event.consume();
				sendChatMessage("Item locked: " + itemName);
			}
		}

		if (event.getMenuOption().startsWith("<col=808080>")) {
			event.consume();
		}
		if ((event.getMenuOption().equals("Trade with") || event.getMenuOption().equals("Accept trade")) && !config.allowTrading()) {
			event.consume();
			sendChatMessage("You stand alone.");
		}
	}

	private void modifyMenuEntries(int itemId) {
		MenuEntry[] menuEntries = client.getMenuEntries();
		List<MenuEntry> newEntries = new ArrayList<>();

		for (MenuEntry entry : menuEntries) {
			String option = entry.getOption();

			if (option.equals("Drop") || option.equals("Destroy") || option.equals("Examine") || option.equals ("Cancel")) {
				newEntries.add(entry);
			} else {
				entry.setOption("<col=808080>" + option);
				entry.setDeprioritized(true);
				newEntries.add(entry);
			}
		}
		client.setMenuEntries(newEntries.toArray(new MenuEntry[0]));
	}

	private void updateSpellbookItemChecks(Widget hoveredSpellWidget) {
		Widget spellPreviewWidget = client.getWidget(MAGIC_SPELL_PREVIEW_GROUP_ID, MAGIC_SPELL_PREVIEW_CHILD_ID);
		Widget[] spellPreviewWidgets = spellPreviewWidget.getDynamicChildren();

		if (spellPreviewWidgets.length == 0 || hoveredSpellWidget == null) {
			return;
		}

		for (int i = 5; i < spellPreviewWidgets.length; i += 2) {
			int itemID = spellPreviewWidgets[i].getItemId();

			if (!unlockedItems.containsValue(itemID) && itemID != -1) {
				spellPreviewWidgets[i+1].setTextColor(16711680); // ff0000
				spellPreviewWidgets[i+1].setText("?/?");

				int spriteId = hoveredSpellWidget.getSpriteId();
				if (!spellSpriteMap.isDisabled(spriteId)) {
					hoveredSpellWidget.setSpriteId(spellSpriteMap.getDisabledSpriteId(spriteId));
				}
			}
		}
	}

	private boolean isShopItemMenu(MenuEntryAdded event) {
		return event.getActionParam1() == 19660816;
	}

	private boolean isShopInventoryMenu(MenuEntryAdded event) {
		return event.getActionParam1() == 19726336;
	}

	private boolean isInventoryItemMenu(MenuEntryAdded event) {
		return event.getActionParam1() == ComponentID.INVENTORY_CONTAINER;
	}

	private void openRandomScapeCategory(Widget widget) {
		widget.setOpacity(SELECTED_OPACITY);

		itemEntries = null;
		clientThread.invokeLater(() -> {
			Widget collectionViewHeader = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_HEADER);
			Widget combatAchievementsButton = client.getWidget(COLLECTION_LOG_GROUP_ID, COMBAT_ACHIEVEMENT_BUTTON);
			combatAchievementsButton.setHidden(true);

			Widget[] headerComponents = collectionViewHeader.getDynamicChildren();

			if (headerComponents.length == 0) {
				return;
			}

			headerComponents[0].setText("RandomScape Unlocks");
			headerComponents[1].setText("Unlocks: <col=ff0000>" + unlockedItems.size());
			if (headerComponents.length > 2) {
				headerComponents[2].setText("");
			}

			createSearchButton(collectionViewHeader);
			createSwapButton(collectionViewHeader);

			Widget collectionView = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW);
			Widget scrollbar = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_SCROLLBAR);
			collectionView.deleteAllChildren();

			int index = 0;
			int x = 0;
			int y = 0;
			int yIncrement = 40;
			int xIncrement = 42;

			for (Map.Entry<Integer, Integer> entry : unlockedItems.entrySet()) {
				Integer detectedItemId = entry.getKey();
				Integer unlockedItemId = entry.getValue();
				boolean tradeable = itemManager.getItemComposition(unlockedItemId).isTradeable();

				if (config.hideUntradeables() && !tradeable) continue;

				addItemToCollectionLog(collectionView, unlockedItemId, x, y, index);
				x = x + xIncrement;
				index++;

				if (x > 210) {
					x = 0;
					y = y + yIncrement;
				}
			}

			collectionView.setScrollHeight(y + 43);
			int scrollHeight = (collectionView.getScrollY() * y) / collectionView.getScrollHeight();
			collectionView.revalidateScroll();
			client.runScript(ScriptID.UPDATE_SCROLLBAR, scrollbar.getId(), collectionView.getId(), scrollHeight);
			collectionView.setScrollY(0);
			scrollbar.setScrollY(0);
		});
	}

	private void createSearchButton(Widget header) {
		searchButton = header.createChild(-1, WidgetType.GRAPHIC);
		searchButton.setSpriteId(SpriteID.GE_SEARCH);
		searchButton.setOriginalWidth(18);
		searchButton.setOriginalHeight(17);
		searchButton.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		searchButton.setOriginalX(5);
		searchButton.setOriginalY(20);
		searchButton.setHasListener(true);
		searchButton.setAction(1, "Search");
		searchButton.setOnOpListener((JavaScriptCallback) e -> openSearch());
		searchButton.revalidate();
	}

	private void createSwapButton(Widget header) {
		swapButton = header.createChild(-1, WidgetType.GRAPHIC);
		swapButton.setSpriteId(SpriteID.SPELL_SPELLBOOK_SWAP);
		swapButton.setOriginalWidth(18);
		swapButton.setOriginalHeight(17);
		swapButton.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
		swapButton.setOriginalX(25);
		swapButton.setOriginalY(20);
		swapButton.setHasListener(true);
		swapButton.setAction(1, "Swap");
		swapButton.setOnOpListener((JavaScriptCallback) e -> openSwap());
		swapButton.revalidate();
	}

	private void openSearch() {
		updateFilter("");
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		searchButton.setAction(1, "Close");
		searchButton.setOnOpListener((JavaScriptCallback) e -> closeSearch());
		searchInput = chatboxPanelManager.openTextInput("Search unlock list")
				.onChanged(s -> clientThread.invokeLater(() -> updateFilter(s.trim())))
				.onClose(() ->
				{
					clientThread.invokeLater(() -> updateFilter(""));
					searchButton.setOnOpListener((JavaScriptCallback) e -> openSearch());
					searchButton.setAction(1, "Open");
				})
				.build();
	}

	private void openSwap() {
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		swapView = !swapView;
		Widget collectionView = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW);
		Widget scrollbar = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_SCROLLBAR);
		collectionView.deleteAllChildren();

		int index = 0;
		int x = 0;
		int y = 0;
		int yIncrement = 40;
		int xIncrement = 42;

		if (!swapView) {
			for (Map.Entry<Integer, Integer> entry : unlockedItems.entrySet()) {
				Integer unlockedItemId = entry.getValue();
				boolean tradeable = itemManager.getItemComposition(unlockedItemId).isTradeable();

				if (config.hideUntradeables() && !tradeable) continue;

				addItemToCollectionLog(collectionView, unlockedItemId, x, y, index);
				x = x + xIncrement;
				index++;

				if (x > 210) {
					x = 0;
					y = y + yIncrement;
				}
			}
		} else {
			for (Map.Entry<Integer, Integer> entry : unlockedItems.entrySet()) {
				Integer detectedItemId = entry.getKey();
				boolean tradeable = itemManager.getItemComposition(detectedItemId).isTradeable();

				if (config.hideUntradeables() && !tradeable) continue;

				addItemToCollectionLog(collectionView, detectedItemId, x, y, index);
				x = x + xIncrement;
				index++;

				if (x > 210) {
					x = 0;
					y = y + yIncrement;
				}
			}
		}

		collectionView.setScrollHeight(y + 43);
		int scrollHeight = (collectionView.getScrollY() * y) / collectionView.getScrollHeight();
		collectionView.revalidateScroll();
		client.runScript(ScriptID.UPDATE_SCROLLBAR, scrollbar.getId(), collectionView.getId(), scrollHeight);
		collectionView.setScrollY(0);
		scrollbar.setScrollY(0);
	}

	private void closeSwap()
	{
		updateFilter("");
		chatboxPanelManager.close();
		client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	private void closeSearch()
	{
		updateFilter("");
		chatboxPanelManager.close();
		client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	private void updateFilter(String input)
	{
		final Widget collectionView = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW);

		if (collectionView == null)
		{
			return;
		}

		String filter = input.toLowerCase();
		updateList(collectionView, filter);
	}

	private void updateList(Widget collectionView, String filter)
	{
		if (itemEntries == null)
		{
			itemEntries = Arrays.stream(collectionView.getDynamicChildren())
					.sorted(Comparator.comparing(Widget::getRelativeY))
					.collect(Collectors.toList());
		}

		itemEntries.forEach(w -> w.setHidden(true));

		Collection<Widget> matchingItems = itemEntries.stream()
				.filter(w -> w.getName().toLowerCase().contains(filter))
				.collect(Collectors.toList());

		int x = 0;
		int y = 0;
		for (Widget entry : matchingItems)
		{
			entry.setHidden(false);
			entry.setOriginalY(y);
			entry.setOriginalX(x);
			entry.revalidate();
			x = x + 42;
			if (x > 210) {
				x = 0;
				y = y + 40;
			}
		}

		y += 43; // y + image height (40) + 3 for padding at the bottom.

		int newHeight = 0;

		if (collectionView.getScrollHeight() > 0)
		{
			newHeight = (collectionView.getScrollY() * y) / collectionView.getScrollHeight();
		}

		collectionView.setScrollHeight(y);
		collectionView.revalidateScroll();

		Widget scrollbar = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_SCROLLBAR);
		client.runScript(
				ScriptID.UPDATE_SCROLLBAR,
				scrollbar.getId(),
				collectionView.getId(),
				newHeight
		);
	}

	private void addItemToCollectionLog(Widget collectionView, Integer itemId, int x, int y, int index) {
		String itemName = itemManager.getItemComposition(itemId).getName();
		Widget newItem = collectionView.createChild(index, 5);
		newItem.setContentType(0);
		newItem.setItemId(itemId);
		newItem.setItemQuantity(1);
		newItem.setItemQuantityMode(0);
		newItem.setModelId(-1);
		newItem.setModelType(1);
		newItem.setSpriteId(-1);
		newItem.setBorderType(1);
		newItem.setFilled(false);
		newItem.setOriginalX(x);
		newItem.setOriginalY(y);
		newItem.setOriginalWidth(36);
		newItem.setOriginalHeight(32);
		newItem.setHasListener(true);
		newItem.setAction(1, "Inspect");
		newItem.setAction(2, "Remove");
		newItem.setOnOpListener((JavaScriptCallback) e -> handleItemAction(itemId, itemName, e));
		newItem.setName(itemName);
		newItem.revalidate();
	}

	private void makeRandomScapeWidget(Widget categories, Widget template, int position, int originalY) {
		Widget randomScapeUnlocks = categories.createChild(position, template.getType());
		randomScapeUnlocks.setText("RandomScape Unlocks");
		randomScapeUnlocks.setName("<col=ff0011>RandomScape Unlocks</col>");
		randomScapeUnlocks.setOpacity(UNSELECTED_OPACITY);
		if (template.hasListener()) {
			randomScapeUnlocks.setHasListener(true);
			randomScapeUnlocks.setAction(1, "View");
			randomScapeUnlocks.setOnOpListener((JavaScriptCallback) e -> openRandomScapeCategory(randomScapeUnlocks));
		}
		randomScapeUnlocks.setBorderType(template.getBorderType());
		randomScapeUnlocks.setItemId(template.getItemId());
		randomScapeUnlocks.setSpriteId(template.getSpriteId());
		randomScapeUnlocks.setOriginalHeight(template.getOriginalHeight());
		randomScapeUnlocks.setOriginalWidth((template.getOriginalWidth()));
		randomScapeUnlocks.setOriginalX(template.getOriginalX());
		randomScapeUnlocks.setOriginalY(originalY);
		randomScapeUnlocks.setXPositionMode(template.getXPositionMode());
		randomScapeUnlocks.setYPositionMode(template.getYPositionMode());
		randomScapeUnlocks.setContentType(template.getContentType());
		randomScapeUnlocks.setItemQuantity(template.getItemQuantity());
		randomScapeUnlocks.setItemQuantityMode(template.getItemQuantityMode());
		randomScapeUnlocks.setModelId(template.getModelId());
		randomScapeUnlocks.setModelType(template.getModelType());
		randomScapeUnlocks.setBorderType(template.getBorderType());
		randomScapeUnlocks.setFilled(template.isFilled());
		randomScapeUnlocks.setTextColor(template.getTextColor());
		randomScapeUnlocks.setFontId(template.getFontId());
		randomScapeUnlocks.setTextShadowed(template.getTextShadowed());
		randomScapeUnlocks.setWidthMode(template.getWidthMode());
		randomScapeUnlocks.setYTextAlignment(template.getYTextAlignment());
		randomScapeUnlocks.revalidate();
	}

	private void handleItemAction(Integer itemId, String itemName, ScriptEvent event) {
		switch (event.getOp()) {
			case MENU_INSPECT:
				final ChatMessageBuilder examination = new ChatMessageBuilder()
						.append(ChatColorType.NORMAL)
						.append("This is an unlocked item called '" + itemName + "'.");

				chatMessageManager.queue(QueuedMessage.builder()
						.type(ChatMessageType.ITEM_EXAMINE)
						.runeLiteFormattedMessage(examination.build())
						.build());
				break;
			case MENU_DELETE:
				clientThread.invokeLater(() -> confirmDeleteItem(itemId, itemName));
				break;
		}
	}

	private void confirmDeleteItem(Integer itemId, String itemName)
	{
		chatboxPanelManager.openTextMenuInput("Do you want to re-lock: " + itemName)
				.option("1. Confirm re-locking of item", () ->
						clientThread.invoke(() ->
						{
							deleteConfirmed = true;
							queueItemDelete(itemId);
							sendChatMessage("Item '" + itemName + "' is no longer unlocked.");
							deleteConfirmed = false;
						})
				)
				.option("2. Cancel", Runnables::doNothing)
				.build();
	}

	private void updateIconOpacity(int groupID, int childID) {
		Widget parentWidget = client.getWidget(groupID, childID);

		if (parentWidget == null) {
			return;
		}

		Widget[] childWidgets = parentWidget.getDynamicChildren();

		if (childWidgets == null) {
			return;
		}

		for (Widget widget : childWidgets){
			if (widget != null) {
				int itemId = widget.getItemId();
				int realItemId = itemManager.canonicalize(itemId);
				ItemComposition itemComposition = itemManager.getItemComposition(realItemId);
				boolean isTradable = itemComposition.isTradeable();

				if (itemId == -1) {
					continue;
				}
				if (!unlockedItems.containsValue(realItemId) && isTradable) {
					widget.setOpacity(200);
				} else {
					widget.setOpacity(0);
				}
			}
		}
	}

	public void unlockItemContainerItems(ItemContainer itemContainer)
	{
		for (Item i : itemContainer.getItems())
		{
			int itemId = i.getId();
			int realItemId = itemManager.canonicalize(itemId);
			ItemComposition itemComposition = itemManager.getItemComposition(itemId);
			int noteId = itemComposition.getNote();
			boolean detectedItemIsTradable = itemComposition.isTradeable();

			if (itemId == -1 || itemId != realItemId && itemId == noteId) {
				continue;
			}

			if (!detectedItemIsTradable && !unlockedItems.containsKey(realItemId) && itemId == realItemId) {
				queueItemUnlock(realItemId, realItemId);
				notifyPlayerOfUnlock(realItemId);
			}

			if (!unlockedItems.containsKey(realItemId))
			{
				int randomItemId = getRandomTradableItem(cachedTradableItems);
				queueItemUnlock(realItemId, randomItemId);
				notifyPlayerOfUnlock(randomItemId);
			}
		}
	}

	private List<Integer> getAllTradableItems()
	{
		List<Integer> tradableItems = new ArrayList<>();

		for (int id = 0; id < client.getItemCount(); id++)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(id);
			if (itemComposition.isTradeable())
			{
				tradableItems.add(id);
			}
		}

		return tradableItems;
	}

	private int getRandomTradableItem(List<Integer> tradableItems)
	{
		Random random = new Random();
		Integer itemId = tradableItems.get(random.nextInt(tradableItems.size()));
		for (int[] range : EXCLUDED_ITEM_IDS) {
			if (itemId >= range[0] && itemId <= range[1]) {
				getRandomTradableItem(tradableItems);
				break;
			}
		}
		return itemId;
	}

	private void notifyPlayerOfUnlock(int unlockedItemId)
	{
		String unlockedItemName = client.getItemDefinition(unlockedItemId).getName();
		if (config.sendNotification())
		{
			notifier.notify("New unlock: " + unlockedItemName);
		}
		else if (config.sendChatMessage())
		{
			sendChatMessage("New unlock: " + unlockedItemName);
		}
	}

	public void queueItemUnlock(int detectedItemId, int unlockedItemId)
	{
		unlockedItems.put(detectedItemId, unlockedItemId);
		randomScapeOverlay.addItemUnlock(unlockedItemId);
		savePlayerUnlocks();
	}

	public void queueItemDelete(int itemId)
	{
		unlockedItems.remove(itemId);
		savePlayerUnlocks();
	}

	private void unlockDefaultItems()
	{
		queueItemUnlock(ItemID.COINS_995, ItemID.COINS_995);
		queueItemUnlock(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND);
	}

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
				.append(chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	void addRandomScapeWidget(int widgetId) {
		Widget logCategories = client.getWidget(COLLECTION_LOG_GROUP_ID, widgetId);
		Widget[] categoryElements = logCategories.getDynamicChildren();
		if (categoryElements.length == 0) {
			return;
		}

		Widget aerialFishing = categoryElements[0];

		if (!(aerialFishing.getText().contains("Aerial Fishing") || aerialFishing.getName().contains("Aerial Fishing"))) {
			return;
		}

		if (categoryElements[categoryElements.length - 1].getText().contains("RandomScape Unlocks")) {
			categoryElements[categoryElements.length - 1].setOpacity(UNSELECTED_OPACITY);
			return;
		}

		int originalY = categoryElements.length * 15;
		makeRandomScapeWidget(logCategories, aerialFishing, categoryElements.length, originalY);

		logCategories.setHeightMode(0);
		logCategories.setOriginalHeight(originalY + 18);
		logCategories.revalidate();
	}

	void updateContainerScroll() {
		Widget categoryContainer = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_CATEGORIES_CONTAINER);
		Widget logCategories = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_CATEGORIES_RECTANGLE);
		Widget[] categoryElements = logCategories.getDynamicChildren();
		int originalHeight = 315; // 21 elements * 15 height
		int scrollHeight = categoryElements.length * 18;

		int newHeight = 0;
		if (categoryContainer.getScrollHeight() > 0 && categoryContainer.getScrollHeight() != scrollHeight)
		{
			newHeight = (categoryContainer.getScrollY() * scrollHeight) / categoryContainer.getScrollHeight();
		}

		categoryContainer.setHeightMode(0);
		categoryContainer.setOriginalHeight(originalHeight);
		categoryContainer.setScrollHeight(scrollHeight);
		categoryContainer.revalidate();
		categoryContainer.revalidateScroll();

		Widget scrollbar = client.getWidget(COLLECTION_LOG_GROUP_ID, COLLECTION_VIEW_CATEGORIES_SCROLLBAR);

		client.runScript(
				ScriptID.UPDATE_SCROLLBAR,
				scrollbar.getId(),
				categoryContainer.getId(),
				newHeight
		);
	}

	void addRandomScapeCategory()
	{
		clientThread.invokeLater(() -> {
			addRandomScapeWidget(COLLECTION_VIEW_CATEGORIES_TEXT);
			addRandomScapeWidget(COLLECTION_VIEW_CATEGORIES_RECTANGLE);
			updateContainerScroll();
		});
	}

	void killSearchResults() {
		Widget grandExchangeSearchResults = client.getWidget(162, GE_SEARCH_RESULTS);

		if (grandExchangeSearchResults == null) {
			return;
		}

		Widget[] children = grandExchangeSearchResults.getDynamicChildren();

		if (children == null || children.length < 2 || children.length % 3 != 0) {
			return;
		}

		for (int i = 0; i < children.length; i += 3) {
			if (!unlockedItems.containsValue(children[i + 2].getItemId())) {
				children[i].setHidden(true);
				children[i + 1].setOpacity(70);
				children[i + 2].setOpacity(70);
			}
		}
	}

	private void savePlayerUnlocks()
	{
		String key = client.getUsername() + "." + CONFIG_KEY;

		if (unlockedItems == null || unlockedItems.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, key);
			return;
		}

		String json = GSON.toJson(unlockedItems);
		configManager.setConfiguration(CONFIG_GROUP, key, json);
	}

	private void loadPlayerUnlocks()
	{
		String key = client.getUsername() + "." + CONFIG_KEY;

		String json = configManager.getConfiguration(CONFIG_GROUP, key);
		unlockedItems.clear();

		if (!Strings.isNullOrEmpty(json))
		{
			// CHECKSTYLE:OFF
			unlockedItems.putAll(GSON.fromJson(json, new TypeToken<Map<Integer, Integer>>(){}.getType()));
			// CHECKSTYLE:ON
		}
	}

	private void updateNamesRandomScape()
	{
		namesRandomScape = Text.fromCSV(config.namesRandomScapers());
	}

	private void updateScreenshotUnlock()
	{
		boolean screenshotUnlock = config.screenshotUnlock();
		boolean includeFrame = config.includeFrame();
		randomScapeOverlay.updateScreenshotUnlock(screenshotUnlock, includeFrame);
	}

	private void addRandomScapeIconToMessage(ChatMessage chatMessage)
	{
		String name = chatMessage.getName();
		if (!name.equals(Text.removeTags(name)))
		{
			return;
		}

		final MessageNode messageNode = chatMessage.getMessageNode();
		messageNode.setName(getNameWithIcon(randomScapeIconOffset, name));

		client.refreshChat();
	}

	private boolean isSeasonalWorld(int worldNumber)
	{
		WorldResult worlds = worldService.getWorlds();
		if (worlds == null)
		{
			return false;
		}

		World world = worlds.findWorld(worldNumber);
		return world != null && world.getTypes().contains(WorldType.SEASONAL);
	}

	private boolean sentByPlayer(ChatMessage chatMessage)
	{
		MessageNode messageNode = chatMessage.getMessageNode();

		return Text.sanitize(messageNode.getName()).equals(Text.sanitize(client.getLocalPlayer().getName()));
	}

	private void setChatboxName(String name)
	{
		Widget chatboxInput = client.getWidget(ComponentID.CHATBOX_INPUT);
		if (chatboxInput != null)
		{
			String text = chatboxInput.getText();
			int idx = text.indexOf(':');
			if (idx != -1)
			{
				String newText = name + text.substring(idx);
				chatboxInput.setText(newText);
			}
		}
	}

	private String getNameChatbox()
	{
		Player player = client.getLocalPlayer();
		if (player != null)
		{
			Widget chatboxInput = client.getWidget(ComponentID.CHATBOX_INPUT);
			String namePlusChannel = player.getName();
			if (chatboxInput != null)
			{
				String text = chatboxInput.getText();
				int idx = text.indexOf(':');
				if (idx != -1)
				{
					namePlusChannel = text.substring(0,idx);
				}
			}
			return getNameWithIcon(randomScapeIconOffset, namePlusChannel);
		}
		return null;
	}

	private String getNameDefault()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		int iconIndex;
		switch (client.getAccountType())
		{
			case IRONMAN:
				iconIndex = IconID.IRONMAN.getIndex();
				break;
			case HARDCORE_IRONMAN:
				iconIndex = IconID.HARDCORE_IRONMAN.getIndex();
				break;
			case ULTIMATE_IRONMAN:
				iconIndex = IconID.ULTIMATE_IRONMAN.getIndex();
				break;
			default:
				return player.getName();
		}

		return getNameWithIcon(iconIndex, player.getName());
	}

	private static String getNameWithIcon(int iconIndex, String name)
	{
		String icon = "<img=" + iconIndex + ">";
		return icon + name;
	}

	private ChatPlayer getChatPlayerFromName(String name)
	{
		// Search friends chat members first, because if a friend is in the friends chat but their private
		// chat is 'off', then we won't know the world
		FriendsChatManager friendsChatManager = client.getFriendsChatManager();
		if (friendsChatManager != null)
		{
			FriendsChatMember friendsChatMember = friendsChatManager.findByName(name);
			if (friendsChatMember != null)
			{
				return friendsChatMember;
			}
		}

		NameableContainer<Friend> friendContainer = client.getFriendContainer();
		return friendContainer.findByName(name);
	}

	private boolean isChatPlayerRandomScaper(String name)
	{
		return isChatPlayerOnNormalWorld(name) && (namesRandomScape.contains(name) || namesRandomScape.contains(name.replace('\u00A0', ' ')));
	}

	private boolean isChatPlayerOnNormalWorld(String name)
	{
		ChatPlayer player = getChatPlayerFromName(name);

		if (player == null)
		{
			return true;
		}

		int world = player.getWorld();
		return !isSeasonalWorld(world);
	}

	private void setupUnlockHistory()
	{
		profileKey = configManager.getRSProfileKey();

		// If profiles are not being used yet, we continue to use the legacy system.
		if (profileKey == null)
		{
			setupLegacyFile();
		}
		else
		{
			setupProfileFile();
		}
	}

	private void setupLegacyFile()
	{
		profileFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + client.getUsername());
		if (!profileFolder.exists())
		{
			profileFolder.mkdirs();
		}

		profileFile = new File(profileFolder, "randomscape-unlocks.txt");
		if (!profileFile.exists())
		{
			try {
				profileFile.createNewFile();
				unlockDefaultItems();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setupProfileFile()
	{
		legacyFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + client.getUsername());
		legacyFile = new File(legacyFolder, "randomscape-unlocks.txt");

		profileFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + profileKey);
		if (!profileFolder.exists())
		{
			profileFolder.mkdirs();
		}
		profileFile = new File(profileFolder, "randomscape-unlocks.txt");
		if (!profileFile.exists())
		{
			if (legacyFile.exists())
			{
				try {
					Files.copy(legacyFile.toPath(), profileFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			else
			{
				try {
					profileFile.createNewFile();
					unlockDefaultItems();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void OnUnlocksCountCommand(ChatMessage chatMessage, String message)
	{
		if (!sentByPlayer(chatMessage))
		{
			return;
		}

		final ChatMessageBuilder builder = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append("You have unlocked ")
				.append(ChatColorType.NORMAL)
				.append(Integer.toString(unlockedItems.size()))
				.append(ChatColorType.HIGHLIGHT)
				.append(" items.");

		String response = builder.build();

		MessageNode messageNode = chatMessage.getMessageNode();
		messageNode.setRuneLiteFormatMessage(response);
		client.refreshChat();
	}

	private void OnUnlocksResetCommand(ChatMessage chatMessage, String message)
	{
		if (!sentByPlayer(chatMessage))
		{
			return;
		}
		resetItemUnlocks();
		sendChatMessage("Unlocks successfully reset!");
	}

	private void resetItemUnlocks(){
		try {
			profileFile.delete();
			unlockedItems.clear();
			savePlayerUnlocks();
			unlockDefaultItems();
			unlockItemContainerItems(client.getItemContainer(InventoryID.INVENTORY));
			unlockItemContainerItems(client.getItemContainer(InventoryID.EQUIPMENT));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void OnUnlocksBackupCommand(ChatMessage chatMessage, String message)
	{
		if (!sentByPlayer(chatMessage))
		{
			return;
		}
		backupItemUnlocks();
		sendChatMessage("Successfully backed up file!");
	}

	private void backupItemUnlocks()
	{
		Path originalPath = profileFile.toPath();
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM_WW_HH_mm_ss");
			Files.copy(originalPath, Paths.get(profileFolder.getPath() + "_" + sdf.format(cal.getTime()) + ".backup"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void loadResources()
	{
		final IndexedSprite[] modIcons = client.getModIcons();

		if (randomScapeIconOffset != -1 || modIcons == null)
		{
			return;
		}

		unlockImage = ImageUtil.getResourceStreamFromClass(getClass(), "/item-unlocked.png");
		BufferedImage image = ImageUtil.getResourceStreamFromClass(getClass(), "/bronzeman_icon.png");
		IndexedSprite indexedSprite = ImageUtil.getImageIndexedSprite(image, client);

		randomScapeIconOffset = modIcons.length;

		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + 1);
		newModIcons[newModIcons.length - 1] = indexedSprite;

		//client.setModIcons(newModIcons);
	}
}