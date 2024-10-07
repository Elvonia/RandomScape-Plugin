package com.kalico.randomscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RandomScapePlugin.CONFIG_GROUP)
public interface RandomScapeConfig extends Config
{
	@ConfigItem(
			keyName = "namesRandomScapers",
			name = "RandomScaper Names",
			position = 1,
			description = "Configures names of RandomScapers to highlight in chat. Format: (name), (name)"
	)
	default String namesRandomScapers()
	{
		return "";
	}

	@ConfigItem(
			keyName = "screenshotUnlock",
			name = "Screenshot new Unlocks",
			position = 2,
			description = "Take a screenshot whenever a new item is unlocked"
	)
	default boolean screenshotUnlock()
	{
		return false;
	}

	@ConfigItem(
			keyName = "includeFrame",
			name = "Include Client Frame",
			description = "Configures whether or not the client frame is included in screenshots",
			position = 3
	)
	default boolean includeFrame()
	{
		return false;
	}

	@ConfigItem(
			keyName = "sendNotification",
			name = "Notify on unlock",
			description = "Send a notification when a new item is unlocked",
			position = 4
	)
	default boolean sendNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "sendChatMessage",
			name = "Chat message on unlock",
			description = "Send a chat message when a new item is unlocked",
			position = 5
	)
	default boolean sendChatMessage()
	{
		return false;
	}

	@ConfigItem(
			keyName = "moveCollectionLogUnlocks",
			name = "Move collection log unlocks",
			description = "Moves the unlocks to the bottom of the 'Other' tab",
			position = 6
	)
	default boolean moveCollectionLogUnlocks()
	{
		return false;
	}

	@ConfigItem(
			keyName = "allowTrading",
			name = "Allow trading",
			description = "Allows the player to trade",
			position = 7
	)
	default boolean allowTrading()
	{
		return false;
	}

	@ConfigItem(
			keyName = "resetCommand",
			name = "Enable reset command",
			description = "Enables the !rsreset command used for wiping your unlocked items",
			position = 8
	)
	default boolean resetCommand()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideUntradeables",
			name = "Hide Untradeable Items",
			description = "Hides untradeable items in the collection log and in chat/notifications",
			position = 9
	)
	default boolean hideUntradeables() {
		return false;
	}
}