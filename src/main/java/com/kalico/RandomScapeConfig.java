package com.kalico;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RandomScapePlugin.CONFIG_GROUP)
public interface RandomScapeConfig extends Config
{
	@ConfigItem(
			keyName = "screenshotUnlock",
			name = "Screenshot new Unlocks",
			position = 0,
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
			position = 1
	)
	default boolean includeFrame()
	{
		return false;
	}

	@ConfigItem(
			keyName = "sendNotification",
			name = "Notify on unlock",
			description = "Send a notification when a new item is unlocked",
			position = 2
	)
	default boolean sendNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "sendChatMessage",
			name = "Chat message on unlock",
			description = "Send a chat message when a new item is unlocked",
			position = 3
	)
	default boolean sendChatMessage()
	{
		return false;
	}

	@ConfigItem(
			keyName = "allowTrading",
			name = "Allow trading",
			description = "Allows the player to trade",
			position = 4
	)
	default boolean allowTrading()
	{
		return false;
	}

	@ConfigItem(
			keyName = "resetCommand",
			name = "Enable reset command",
			description = "Enables the !rsreset command used for wiping your unlocked items",
			position = 5
	)
	default boolean resetCommand()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideUntradeables",
			name = "Hide Untradeable Items",
			description = "Hides untradeable items in the collection log and in chat/notifications",
			position = 6
	)
	default boolean hideUntradeables() {
		return false;
	}
}