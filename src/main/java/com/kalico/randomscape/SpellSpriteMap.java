package com.kalico.randomscape;

import java.util.HashMap;
import java.util.Map;

public class SpellSpriteMap {
    private final Map<Integer, Integer> spellToDisabledMap = new HashMap<>();

    public void initialize() {
        spellToDisabledMap.put(356, 356); // Home Teleport -> Home Teleport Disabled
        spellToDisabledMap.put(15, 65); // Wind Strike -> Wind Strike Disabled
        spellToDisabledMap.put(16, 66); // Confuse -> Confuse Disabled
        spellToDisabledMap.put(358, 358); // Crossbow Bolt Enchantments -> Crossbow Bolt Enchantments Disabled
        spellToDisabledMap.put(17, 67); // Water Strike -> Water Strike Disabled
        spellToDisabledMap.put(366, 366); // Jewellery Enchantments -> Jewellery Enchantments Disabled
        spellToDisabledMap.put(1765, 1845); // Lvl-1 Enchant -> Lvl-1 Enchant Disabled
        spellToDisabledMap.put(19, 69); // Earth Strike -> Earth Strike Disabled
        spellToDisabledMap.put(20, 70); // Weaken -> Weaken Disabled
        spellToDisabledMap.put(21, 71); // Fire Strike -> Fire Strike Disabled
        spellToDisabledMap.put(22, 72); // Bones to Bananas -> Bones to Bananas Disabled
        spellToDisabledMap.put(23, 73); // Wind Bolt -> Wind Bolt Disabled
        spellToDisabledMap.put(24, 74); // Curse -> Curse Disabled
        spellToDisabledMap.put(319, 369); // Bind -> Bind Disabled
        spellToDisabledMap.put(25, 75); // Low Level Alchemy -> Low Level Alchemy Disabled
        spellToDisabledMap.put(26, 76); // Water Bolt -> Water Bolt Disabled
        spellToDisabledMap.put(27, 77); // Varrock Teleport -> Varrock Teleport Disabled
        spellToDisabledMap.put(1766, 1846); // Lvl-2 Enchant -> Lvl-2 Enchant Disabled
        spellToDisabledMap.put(29, 79); // Earth Bolt -> Earth Bolt Disabled
        spellToDisabledMap.put(30, 80); // Lumbridge Teleport -> Lumbridge Teleport Disabled
        spellToDisabledMap.put(31, 81); // Telekinetic Grab -> Telekinetic Grab Disabled
        spellToDisabledMap.put(32, 82); // Fire Bolt -> Fire Bolt Disabled
        spellToDisabledMap.put(33, 83); // Falador Teleport -> Falador Teleport Disabled
        spellToDisabledMap.put(34, 84); // Crumble Undead -> Crumble Undead Disabled
        spellToDisabledMap.put(355, 405); // Teleport to House -> Teleport to House Disabled
        spellToDisabledMap.put(35, 85); // Wind Blast -> Wind Blast Disabled
        spellToDisabledMap.put(36, 86); // Superheat Item -> Superheat Item Disabled
        spellToDisabledMap.put(37, 87); // Camelot Teleport -> Camelot Teleport Disabled
        spellToDisabledMap.put(38, 88); // Water Blast -> Water Blast Disabled
        spellToDisabledMap.put(360, 410); // Kourend Castle Teleport -> Kourend Castle Teleport Disabled
        spellToDisabledMap.put(1767, 1847); // Lvl-3 Enchant -> Lvl-3 Enchant Disabled
        spellToDisabledMap.put(53, 103); // Iban Blast -> Iban Blast Disabled
        spellToDisabledMap.put(320, 370); // Snare -> Snare Disabled
        spellToDisabledMap.put(324, 374); // Magic Dart -> Magic Dart Disabled
        spellToDisabledMap.put(54, 104); // Ardougne Teleport -> Ardougne Teleport Disabled
        spellToDisabledMap.put(40, 90); // Earth Blast -> Earth Blast Disabled
        spellToDisabledMap.put(367, 417); // Civitas illa Fortis Teleport -> Civitas illa Fortis Teleport Disabled
        spellToDisabledMap.put(41, 91); // High Level Alchemy -> High Level Alchemy Disabled
        spellToDisabledMap.put(42, 92); // Charge Water Orb -> Charge Water Orb Disabled
        spellToDisabledMap.put(1768, 1848); // Lvl-4 Enchant -> Lvl-4 Enchant Disabled
        spellToDisabledMap.put(55, 105); // Watchtower Teleport -> Watchtower Teleport Disabled
        spellToDisabledMap.put(44, 94); // Fire Blast -> Fire Blast Disabled
        spellToDisabledMap.put(45, 95); // Charge Earth Orb -> Charge Earth Orb Disabled
        spellToDisabledMap.put(354, 404); // Bones to Peaches -> Bones to Peaches Disabled
        spellToDisabledMap.put(61, 111); // Saradomin Strike -> Saradomin Strike Disabled
        spellToDisabledMap.put(60, 110); // Claws of Guthix -> Claws of Guthix Disabled
        spellToDisabledMap.put(59, 109); // Flames of Zamorak -> Flames of Zamorak Disabled
        spellToDisabledMap.put(323, 373); // Trollheim Teleport -> Trollheim Teleport Disabled
        spellToDisabledMap.put(46, 96); // Wind Wave -> Wind Wave Disabled
        spellToDisabledMap.put(47, 97); // Charge Fire Orb -> Charge Fire Orb Disabled
        spellToDisabledMap.put(357, 407); // Ape Atoll Teleport -> Ape Atoll Teleport Disabled
        spellToDisabledMap.put(48, 98); // Water Wave -> Water Wave Disabled
        spellToDisabledMap.put(49, 99); // Charge Air Orb -> Charge Air Orb Disabled
        spellToDisabledMap.put(56, 106); // Vulnerability -> Vulnerability Disabled
        spellToDisabledMap.put(1769, 1849); // Lvl-5 Enchant -> Lvl-5 Enchant Disabled
        spellToDisabledMap.put(51, 101); // Earth Wave -> Earth Wave Disabled
        spellToDisabledMap.put(57, 107); // Enfeeble -> Enfeeble Disabled
        spellToDisabledMap.put(349, 399); // Teleother Lumbridge -> Teleother Lumbridge Disabled
        spellToDisabledMap.put(52, 102); // Fire Wave -> Fire Wave Disabled
        spellToDisabledMap.put(321, 371); // Entangle -> Entangle Disabled
        spellToDisabledMap.put(58, 108); // Stun -> Stun Disabled
        spellToDisabledMap.put(322, 372); // Charge -> Charge Disabled
        spellToDisabledMap.put(362, 412); // Wind Surge -> Wind Surge Disabled
        spellToDisabledMap.put(350, 400); // Teleother Falador -> Teleother Falador Disabled
        spellToDisabledMap.put(363, 413); // Water Surge -> Water Surge Disabled
        spellToDisabledMap.put(352, 402); // Tele Block -> Tele Block Disabled
        spellToDisabledMap.put(359, 409); // Teleport to Target -> Teleport to Target Disabled
        spellToDisabledMap.put(1770, 1850); // Lvl-6 Enchant -> Lvl-6 Enchant Disabled
        spellToDisabledMap.put(351, 401); // Teleother Camelot -> Teleother Camelot Disabled
        spellToDisabledMap.put(364, 414); // Earth Surge -> Earth Surge Disabled
        spellToDisabledMap.put(1771, 1851); // Lvl-7 Enchant -> Lvl-7 Enchant Disabled
        spellToDisabledMap.put(365, 415); // Fire Surge -> Fire Surge Disabled
        spellToDisabledMap.put(325, 375); // Ice Rush -> Ice Rush Disabled
        spellToDisabledMap.put(327, 377); // Ice Blitz -> Ice Blitz Disabled
        spellToDisabledMap.put(326, 376); // Ice Burst -> Ice Burst Disabled
        spellToDisabledMap.put(328, 378); // Ice Barrage -> Ice Barrage Disabled
        spellToDisabledMap.put(333, 383); // Blood Rush -> Blood Rush Disabled
        spellToDisabledMap.put(335, 385); // Blood Blitz -> Blood Blitz Disabled
        spellToDisabledMap.put(334, 384); // Blood Burst -> Blood Burst Disabled
        spellToDisabledMap.put(336, 386); // Blood Barrage -> Blood Barrage Disabled
        spellToDisabledMap.put(329, 379); // Smoke Rush -> Smoke Rush Disabled
        spellToDisabledMap.put(331, 381); // Smoke Blitz -> Smoke Blitz Disabled
        spellToDisabledMap.put(330, 380); // Smoke Burst -> Smoke Burst Disabled
        spellToDisabledMap.put(332, 382); // Smoke Barrage -> Smoke Barrage Disabled
        spellToDisabledMap.put(337, 387); // Shadow Rush -> Shadow Rush Disabled
        spellToDisabledMap.put(339, 389); // Shadow Blitz -> Shadow Blitz Disabled
        spellToDisabledMap.put(338, 388); // Shadow Burst -> Shadow Burst Disabled
        spellToDisabledMap.put(340, 390); // Shadow Barrage -> Shadow Barrage Disabled
        spellToDisabledMap.put(341, 391); // Paddewwa Teleport -> Paddewwa Teleport Disabled
        spellToDisabledMap.put(342, 392); // Senntisten Teleport -> Senntisten Teleport Disabled
        spellToDisabledMap.put(343, 393); // Kharyrll Teleport -> Kharyrll Teleport Disabled
        spellToDisabledMap.put(344, 394); // Lassar Teleport -> Lassar Teleport Disabled
        spellToDisabledMap.put(345, 395); // Dareeyak Teleport -> Dareeyak Teleport Disabled
        spellToDisabledMap.put(346, 396); // Carrallanger Teleport -> Carrallanger Teleport Disabled
        spellToDisabledMap.put(347, 397); // Annakarl Teleport -> Annakarl Teleport Disabled
        spellToDisabledMap.put(348, 398); // Ghorrock Teleport -> Ghorrock Teleport Disabled
        spellToDisabledMap.put(543, 593); // Bake Pie -> Bake Pie Disabled
        spellToDisabledMap.put(567, 617); // Cure Plant -> Cure Plant Disabled
        spellToDisabledMap.put(577, 627); // Monster Examine -> Monster Examine Disabled
        spellToDisabledMap.put(568, 618); // NPC Contact -> NPC Contact Disabled
        spellToDisabledMap.put(559, 609); // Cure Other -> Cure Other Disabled
        spellToDisabledMap.put(578, 628); // Humidify -> Humidify Disabled
        spellToDisabledMap.put(544, 594); // Moonclan Teleport -> Moonclan Teleport Disabled
        spellToDisabledMap.put(569, 619); // Tele Group Moonclan -> Tele Group Moonclan Disabled
        spellToDisabledMap.put(562, 612); // Cure Me -> Cure Me Disabled
        spellToDisabledMap.put(579, 629); // Hunter Kit -> Hunter Kit Disabled
        spellToDisabledMap.put(545, 595); // Waterbirth Teleport -> Waterbirth Teleport Disabled
        spellToDisabledMap.put(570, 620); // Tele Group Waterbirth -> Tele Group Waterbirth Disabled
        spellToDisabledMap.put(565, 615); // Cure Group -> Cure Group Disabled
        spellToDisabledMap.put(576, 626); // Stat Spy -> Stat Spy Disabled
        spellToDisabledMap.put(547, 597); // Barbarian Teleport -> Barbarian Teleport Disabled
        spellToDisabledMap.put(571, 621); // Tele Group Barbarian -> Tele Group Barbarian Disabled
        spellToDisabledMap.put(548, 598); // Superglass Make -> Superglass Make Disabled
        spellToDisabledMap.put(583, 633); // Tan Leather -> Tan Leather Disabled
        spellToDisabledMap.put(549, 599); // Khazard Teleport -> Khazard Teleport Disabled
        spellToDisabledMap.put(572, 622); // Tele Group Khazard -> Tele Group Khazard Disabled
        spellToDisabledMap.put(580, 630); // Dream -> Dream Disabled
        spellToDisabledMap.put(550, 600); // String Jewellery -> String Jewellery Disabled
        spellToDisabledMap.put(554, 604); // Stat Restore Pot Share -> Stat Restore Pot Share Disabled
        spellToDisabledMap.put(552, 602); // Magic Imbue -> Magic Imbue Disabled
        spellToDisabledMap.put(553, 603); // Fertile Soil -> Fertile Soil Disabled
        spellToDisabledMap.put(551, 604); // Boost Potion Share -> Boost Potion Share Disabled
        spellToDisabledMap.put(555, 605); // Fishing Guild Teleport -> Fishing Guild Teleport Disabled
        spellToDisabledMap.put(573, 623); // Tele Group Fishing Guild -> Tele Group Fishing Guild Disabled
        spellToDisabledMap.put(581, 631); // Plank Make -> Plank Make Disabled
        spellToDisabledMap.put(556, 606); // Catherby Teleport -> Catherby Teleport Disabled
        spellToDisabledMap.put(574, 624); // Tele Group Catherby -> Tele Group Catherby Disabled
        spellToDisabledMap.put(584, 634); // Recharge Dragonstone -> Recharge Dragonstone Disabled
        spellToDisabledMap.put(557, 607); // Ice Plateau Teleport -> Ice Plateau Teleport Disabled
        spellToDisabledMap.put(575, 625); // Tele Group Ice Plateau -> Tele Group Ice Plateau Disabled
        spellToDisabledMap.put(558, 608); // Energy Transfer -> Energy Transfer Disabled
        spellToDisabledMap.put(560, 610); // Heal Other -> Heal Other Disabled
        spellToDisabledMap.put(561, 611); // Vengeance Other -> Vengeance Other Disabled
        spellToDisabledMap.put(564, 614); // Vengeance -> Vengeance Disabled
        spellToDisabledMap.put(566, 616); // Heal Group -> Heal Group Disabled
        spellToDisabledMap.put(582, 632); // Spellbook Swap -> Spellbook Swap Disabled
        spellToDisabledMap.put(563, 613); // Geomancy -> Geomancy Disabled
        spellToDisabledMap.put(585, 635); // Spin Flax -> Spin Flax Disabled
        spellToDisabledMap.put(586, 636); // Ourania Teleport -> Ourania Teleport Disabled
        spellToDisabledMap.put(1251, 1251); // Arceuus Home Teleport -> Arceuus Home Teleport Disabled
        spellToDisabledMap.put(1247, 1272); // Basic Reanimation -> Basic Reanimation Disabled
        spellToDisabledMap.put(1252, 1277); // Arceuus Library Teleport -> Arceuus Library Teleport Disabled
        spellToDisabledMap.put(1248, 1273); // Adept Reanimation -> Adept Reanimation Disabled
        spellToDisabledMap.put(1249, 1274); // Expert Reanimation -> Expert Reanimation Disabled
        spellToDisabledMap.put(1250, 1275); // Master Reanimation -> Master Reanimation Disabled
        spellToDisabledMap.put(1253, 1278); // Draynor Manor Teleport -> Draynor Manor Teleport Disabled
        spellToDisabledMap.put(1256, 1281); // Mind Altar Teleport -> Mind Altar Teleport Disabled
        spellToDisabledMap.put(1257, 1282); // Respawn Teleport -> Respawn Teleport Disabled
        spellToDisabledMap.put(1258, 1283); // Salve Graveyard Teleport -> Salve Graveyard Teleport Disabled
        spellToDisabledMap.put(1259, 1284); // Fenkenstrain's Castle Teleport -> Fenkenstrain's Castle Teleport Disabled
        spellToDisabledMap.put(1260, 1285); // West Ardougne Teleport -> West Ardougne Teleport Disabled
        spellToDisabledMap.put(1261, 1286); // Harmony Island Teleport -> Harmony Island Teleport Disabled
        spellToDisabledMap.put(1264, 1289); // Cemetery Teleport -> Cemetery Teleport Disabled
        spellToDisabledMap.put(1266, 1291); // Resurrect Crops -> Resurrect Crops Disabled
        spellToDisabledMap.put(1262, 1287); // Barrows Teleport -> Barrows Teleport Disabled
        spellToDisabledMap.put(1263, 1288); // Ape Atoll Teleport -> Ape Atoll Teleport Disabled
        spellToDisabledMap.put(1255, 1255); // Battlefront Teleport -> Battlefront Teleport Disabled
        spellToDisabledMap.put(1302, 1321); // Inferior Demonbane -> Inferior Demonbane Disabled
        spellToDisabledMap.put(1303, 1322); // Superior Demonbane -> Superior Demonbane Disabled
        spellToDisabledMap.put(1304, 1323); // Dark Demonbane -> Dark Demonbane Disabled
        spellToDisabledMap.put(1305, 1324); // Mark of Darkness -> Mark of Darkness Disabled
        spellToDisabledMap.put(1267, 1292); // Ghostly Grasp -> Ghostly Grasp Disabled
        spellToDisabledMap.put(1268, 1293); // Skeletal Grasp -> Skeletal Grasp Disabled
        spellToDisabledMap.put(1269, 1294); // Undead Grasp -> Undead Grasp Disabled
        spellToDisabledMap.put(1306, 1325); // Ward of Arceuus -> Ward of Arceuus Disabled
        spellToDisabledMap.put(1307, 1326); // Lesser Corruption -> Lesser Corruption Disabled
        spellToDisabledMap.put(1308, 1327); // Greater Corruption -> Greater Corruption Disabled
        spellToDisabledMap.put(1311, 1330); // Demonic Offering -> Demonic Offering Disabled
        spellToDisabledMap.put(1312, 1331); // Sinister Offering -> Sinister Offering Disabled
        spellToDisabledMap.put(1318, 1318); // Degrime -> Degrime Disabled
        spellToDisabledMap.put(1315, 1334); // Shadow Veil -> Shadow Veil Disabled
        spellToDisabledMap.put(1317, 1336); // Vile Vigour -> Vile Vigour Disabled
        spellToDisabledMap.put(1316, 1335); // Dark Lure -> Dark Lure Disabled
        spellToDisabledMap.put(1310, 1329); // Death Charge -> Death Charge Disabled
        spellToDisabledMap.put(1270, 1295); // Resurrect Lesser Ghost -> Resurrect Lesser Ghost Disabled
        spellToDisabledMap.put(1271, 1296); // Resurrect Lesser Skeleton -> Resurrect Lesser Skeleton Disabled
        spellToDisabledMap.put(1300, 1319); // Resurrect Lesser Zombie -> Resurrect Lesser Zombie Disabled
        spellToDisabledMap.put(2979, 2985); // Resurrect Superior Ghost -> Resurrect Superior Ghost Disabled
        spellToDisabledMap.put(2981, 2987); // Resurrect Superior Skeleton -> Resurrect Superior Skeleton Disabled
        spellToDisabledMap.put(2983, 2989); // Resurrect Superior Zombie -> Resurrect Superior Zombie Disabled
        spellToDisabledMap.put(2980, 2986); // Resurrect Greater Ghost -> Resurrect Greater Ghost Disabled
        spellToDisabledMap.put(2982, 2988); // Resurrect Greater Skeleton -> Resurrect Greater Skeleton Disabled
        spellToDisabledMap.put(2984, 2990); // Resurrect Greater Zombie -> Resurrect Greater Zombie Disabled
    }

    public void shutdown() {
        spellToDisabledMap.clear();
    }

    public Integer getDisabledSpriteId(int spriteId) {
        return spellToDisabledMap.get(spriteId);
    }

    public boolean isDisabled(int spriteId) {
        return spellToDisabledMap.containsValue(spriteId);
    }
}
