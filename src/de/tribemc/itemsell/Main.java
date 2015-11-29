package de.tribemc.itemsell;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	Plugin pl = this;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, pl);
		setup();
		File f = new File("plugins/SkySell/");
		if (!f.exists())
			f.mkdir();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(pl);
		super.onDisable();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& e.getPlayer().getWorld().getName()
						.equalsIgnoreCase("SkyPvP")
				&& e.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
			openSellChest(e.getPlayer());
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equals("§c§lVerkaufen")) {
			Player p = ((Player) e.getPlayer());
			double level = (getLevelForItems(e.getInventory().getContents()) - 1D);
			if (level > 0) {
				System.out.println("[SkySell] " + p.getName() + " hat " + level
						+ " erhalten!");
				p.setLevel(p.getLevel() + (int) level);
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSpielerQuit(PlayerQuitEvent e) {
		final int level = e.getPlayer().getLevel();
		final String playerName = e.getPlayer().getName().toLowerCase();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				File f = new File("plugins/SkySell/" + playerName + ".yml");
				if (!f.exists())
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
				if (cfg == null)
					return;
				int count = level;
				int i = 0;
				// 2000 Level Steuerfrei
				if (count < 1000) {
					i += count;
					count = 0;
				} else {
					i += 1000;
					count -= 1000;
				}
				// nächsten 2000 mit 20%
				if (count > 0)
					if (count < 2000) {
						i += count * 0.8;
						count = 0;
					} else {
						i += 2000 * 0.8;
						count -= 2000;
					}
				// nächsten 5000 mit 25%
				if (count > 0)
					if (count < 5000) {
						i += count * 0.85;
						count = 0;
					} else {
						i += 5000 * 0.85;
						count -= 5000;
					}

				// nächsten 7500 mit 30%
				if (count > 0)
					if (count < 7500) {
						i += count * 0.70;
						count = 0;
					} else {
						i += 7500 * 0.70;
						count -= 7500;
					}

				// nächsten 10000 mit 40%
				if (count > 0)
					if (count < 10000) {
						i += count * 0.60;
						count = 0;
					} else {
						i += 10000 * 0.60;
						count -= 10000;
					}

				// nächsten 15000 mit 55%
				if (count > 0)
					if (count < 15000) {
						i += count * 0.45;
						count = 0;
					} else {
						i += 15000 * 0.45;
						count -= 15000;
					}

				// nächsten 17500 mit 70%
				if (count > 0)
					if (count < 17500) {
						i += count * 0.30;
						count = 0;
					} else {
						i += 17500 * 0.30;
						count -= 17500;
					}

				// nächsten 25000 mit 80%
				if (count > 0)
					if (count < 25000) {
						i += count * 0.2;
						count = 0;
					} else {
						i += 25000 * 0.20;
						count -= 25000;
					}

				// nächsten 50000 mit 90%
				if (count > 0)
					if (count < 50000) {
						i += count * 0.1;
						count = 0;
					} else {
						i += 50000 * 0.10;
						count -= 50000;
					}
				// 99 %
				if (count > 0)
					if (count < 750000) {
						i += count * 0.01;
						count = 0;
					} else {
						i += 750000 * 0.01;
						count -= 750000;
					}
				i += count * 0.005;

				cfg.set("Sky.Level", i);

				try {
					cfg.save(f);
					System.out.println("[SkySell] Es wurden " + i
							+ " Level von " + level + " für " + playerName
							+ " gespeichert!");
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		});
	}

	private void openSellChest(Player player) {
		player.openInventory(Bukkit.createInventory(null, 36, "§c§lVerkaufen"));
		player.sendMessage("§8===-- §c§lItem-Verkauf §8--===\n\n§7Du kannst deine Items gegen Level eintauschen!\n\n§7Schritt 1: §9Lege Items in die Kiste!\n§7Schritt 2: §9Schließe die Kiste!\n\n§7Bei genügend Items bekommst du die Level ausbezahlt!");
	}

	private LinkedHashMap<Material, Double> price;

	@SuppressWarnings("deprecation")
	private void setup() {
		this.price = new LinkedHashMap<>();
		this.price.put(Material.AIR, 0.0);
		// 32 Steine
		this.price.put(Material.STONE, 0.03125);
		// 16 Gras
		this.price.put(Material.GRASS, 0.0625);
		// 64 Erde
		this.price.put(Material.DIRT, 0.015625);
		// 64 Kobble
		this.price.put(Material.COBBLESTONE, 0.015625);
		// 256 Holzbretter
		this.price.put(Material.WOOD, 0.00390625);
		// 64 Sätzlinge
		this.price.put(Material.SAPLING, 0.015625);
		this.price.put(Material.BEDROCK, 0.0);
		this.price.put(Material.WATER, 0.0);
		this.price.put(Material.STATIONARY_WATER, 0.0);
		// 1/4 Lava
		this.price.put(Material.LAVA, 4.0);
		this.price.put(Material.STATIONARY_LAVA, 0.0);
		// 1 Sand
		this.price.put(Material.SAND, 6.0);
		// 8 Kies
		this.price.put(Material.GRAVEL, 0.125);
		this.price.put(Material.GOLD_ORE, 0.0);
		this.price.put(Material.IRON_ORE, 0.0);
		this.price.put(Material.COAL_ORE, 0.0);
		// 64 Holzstämme
		this.price.put(Material.LOG, 0.015625);
		// 48 Blätter
		this.price.put(Material.LEAVES, 0.020833);
		// 1/30 Schwamm
		this.price.put(Material.SPONGE, 65.0);
		// 8 Glas
		this.price.put(Material.GLASS, 0.125);
		this.price.put(Material.LAPIS_ORE, 0.0);
		// 30 Lapis
		this.price.put(Material.LAPIS_BLOCK, 30.0);
		// 1 Dispenser
		this.price.put(Material.DISPENSER, 1.0);
		// 5 Sandstein
		this.price.put(Material.SANDSTONE, 0.2);
		// 1/7 Notenblöcke
		this.price.put(Material.NOTE_BLOCK, 7.0);
		// 1/4 Betten
		this.price.put(Material.BED_BLOCK, 4.0);
		// 5 Powerschienen
		this.price.put(Material.POWERED_RAIL, 0.2);
		// 2 Detectorschienen
		this.price.put(Material.DETECTOR_RAIL, 0.5);
		// 1 kleber Kolben
		this.price.put(Material.PISTON_STICKY_BASE, 1.0);
		this.price.put(Material.WEB, 10.0);
		this.price.put(Material.LONG_GRASS, 0.0);
		this.price.put(Material.DEAD_BUSH, 0.0);
		// 2 Normale Kolben
		this.price.put(Material.PISTON_BASE, 0.5);
		this.price.put(Material.PISTON_EXTENSION, 0.0);
		// 10 Wolle
		this.price.put(Material.WOOL, 0.1);
		this.price.put(Material.PISTON_MOVING_PIECE, 0.0);
		// 64 Blumen
		this.price.put(Material.YELLOW_FLOWER, 0.015625);
		this.price.put(Material.RED_ROSE, 0.015625);
		// 16 Pilze
		this.price.put(Material.BROWN_MUSHROOM, 0.0625);
		this.price.put(Material.RED_MUSHROOM, 0.0625);
		// 1/30 Goldblock
		this.price.put(Material.GOLD_BLOCK, 39.0);
		// 1/25 Eisenblock
		this.price.put(Material.IRON_BLOCK, 35.0);
		this.price.put(Material.DOUBLE_STEP, 0.0);
		// 64 Stufen
		this.price.put(Material.STEP, 0.015625);
		// 16 Bricks
		this.price.put(Material.BRICK, 0.0625);
		// 8 Tnt
		this.price.put(Material.TNT, 0.125);
		// 12 Bücherregale
		this.price.put(Material.BOOKSHELF, 0.08333);
		// 16 bemoosten Steine
		this.price.put(Material.MOSSY_COBBLESTONE, 0.0625);
		// 1/5 Obsidian
		this.price.put(Material.OBSIDIAN, 5.0);
		// 64 Fackeln
		this.price.put(Material.TORCH, 0.015625);
		this.price.put(Material.FIRE, 0.0);
		this.price.put(Material.MOB_SPAWNER, 0.0);
		// 64 Holzstufen
		this.price.put(Material.WOOD_STAIRS, 0.015625);
		// 16 Kisten
		this.price.put(Material.CHEST, 0.0625);
		// 8 Redstone Wire
		this.price.put(Material.REDSTONE_WIRE, 0.0125);
		// 1/5 Diamant-Erz
		this.price.put(Material.DIAMOND_ORE, 5.0);
		// 1/50 Diamntblock
		this.price.put(Material.DIAMOND_BLOCK, 80.0);
		// 64 Workbenches
		this.price.put(Material.WORKBENCH, 0.015625);
		// 1000 Samen
		this.price.put(Material.CROPS, 0.001);
		this.price.put(Material.SOIL, 0.0);
		// 8 Öfen
		this.price.put(Material.FURNACE, 1.0);
		this.price.put(Material.BURNING_FURNACE, 0.0);
		// 16 Schilder
		this.price.put(Material.SIGN_POST, 0.0625);
		// 2 Türen
		this.price.put(Material.WOODEN_DOOR, 0.5);
		// 32 Leitern
		this.price.put(Material.LADDER, 0.03125);
		// XX Schienen
		this.price.put(Material.RAILS, 0.0625);
		// 100 KobbleStufen
		this.price.put(Material.COBBLESTONE_STAIRS, 0.01);
		this.price.put(Material.WALL_SIGN, 0.005);
		// 200 Hebel
		this.price.put(Material.LEVER, 0.005);
		// 150 Steinplatten
		this.price.put(Material.STONE_PLATE, 0.0066);
		// 5 Eisentüren
		this.price.put(Material.IRON_DOOR_BLOCK, 0.1);
		// 300 Holzplatten
		this.price.put(Material.WOOD_PLATE, 0.0033);
		this.price.put(Material.REDSTONE_ORE, 0.0);
		this.price.put(Material.GLOWING_REDSTONE_ORE, 0.0);
		// 50 Redstonefackeln
		this.price.put(Material.REDSTONE_TORCH_OFF, 0.02);
		this.price.put(Material.REDSTONE_TORCH_ON, 0.02);
		// 300 Steinknöpfe
		this.price.put(Material.STONE_BUTTON, 0.0033);
		// 1000 Schnee
		this.price.put(Material.SNOW, 0.001);
		// 80 Eis
		this.price.put(Material.ICE, 0.0125);
		// 120 Schneeblöcke
		this.price.put(Material.SNOW_BLOCK, 0.00833);
		// 100 Kakteen
		this.price.put(Material.CACTUS, 0.01);
		// 120 Clay
		this.price.put(Material.CLAY, 0.00833);
		this.price.put(Material.SUGAR_CANE_BLOCK, 0.0);
		// 11 Plattenspieler
		this.price.put(Material.JUKEBOX, 0.09);
		// 100 Zäune
		this.price.put(Material.FENCE, 0.01);
		this.price.put(Material.PUMPKIN, 0.0);
		// 1.25 Nethersteine
		this.price.put(Material.NETHERRACK, 0.8);
		// 1.6 Seelensand
		this.price.put(Material.SOUL_SAND, 5.0);
		// 75 Glowstone
		this.price.put(Material.GLOWSTONE, 3.5);
		this.price.put(Material.PORTAL, 0.0);
		// 60 Kübislaternen
		this.price.put(Material.JACK_O_LANTERN, 4.0);
		// 10 Kuchen
		this.price.put(Material.CAKE_BLOCK, 0.1);
		// 100 Dioden
		this.price.put(Material.DIODE_BLOCK_OFF, 0.01);
		this.price.put(Material.DIODE_BLOCK_ON, 0.01);
		this.price.put(Material.LOCKED_CHEST, 0.0);
		this.price.put(Material.STAINED_GLASS, 0.2);
		this.price.put(Material.TRAP_DOOR, 0.001);
		this.price.put(Material.MONSTER_EGGS, 50.0);
		this.price.put(Material.SMOOTH_BRICK, 0.29166);
		this.price.put(Material.HUGE_MUSHROOM_1, 0.1);
		this.price.put(Material.HUGE_MUSHROOM_2, 0.1);
		// 100 Eisenzäune
		this.price.put(Material.IRON_FENCE, 0.01);
		this.price.put(Material.THIN_GLASS, 0.01);
		// 1000 Melonen
		this.price.put(Material.MELON_BLOCK, 0.001);
		// 100 Stängel
		this.price.put(Material.PUMPKIN_STEM, 0.01);
		this.price.put(Material.MELON_STEM, 0.01);
		// 1000 Ranken
		this.price.put(Material.VINE, 0.001);
		// 50 ZaunTüren
		this.price.put(Material.FENCE_GATE, 0.02);
		// ?
		this.price.put(Material.BRICK_STAIRS, 0.001);
		this.price.put(Material.SMOOTH_STAIRS, 0.001);
		this.price.put(Material.MYCEL, 3.5);
		this.price.put(Material.WATER_LILY, 0.01);
		this.price.put(Material.NETHER_BRICK, 0.18);
		this.price.put(Material.NETHER_FENCE, 0.18);
		this.price.put(Material.NETHER_BRICK_STAIRS, 0.0);
		this.price.put(Material.NETHER_WARTS, 0.05);
		// 10 Enchantment Table
		this.price.put(Material.ENCHANTMENT_TABLE, 15.0);
		// 5 Braustände
		this.price.put(Material.BREWING_STAND, 30.0);
		// 13 Kessel
		this.price.put(Material.CAULDRON, 0.08);
		this.price.put(Material.ENDER_PORTAL, 0.0);
		// 1 Enderrahmen
		this.price.put(Material.ENDER_PORTAL_FRAME, 1.0);
		// 10 End-Stein
		this.price.put(Material.ENDER_STONE, 12.0);
		// 1/4 Dracheneier
		this.price.put(Material.DRAGON_EGG, 40.0);
		// 65 Redstonelampen
		this.price.put(Material.REDSTONE_LAMP_OFF, 4.5);
		this.price.put(Material.REDSTONE_LAMP_ON, 4.5);
		// 500 HolzDoppelstufen
		this.price.put(Material.WOOD_DOUBLE_STEP, 0.18);
		// 333 Holzstufen
		this.price.put(Material.WOOD_STEP, 0.18);
		// 100 Kakao
		this.price.put(Material.COCOA, 0.05);
		this.price.put(Material.SANDSTONE_STAIRS, 1.66);
		this.price.put(Material.EMERALD_ORE, 0.0);
		this.price.put(Material.ENDER_CHEST, 10.0);
		this.price.put(Material.TRIPWIRE_HOOK, 0.1);
		this.price.put(Material.TRIPWIRE, 0.1);
		// 1/3 Emerald Block
		this.price.put(Material.EMERALD_BLOCK, 60.0);
		// 333 Holzstufen
		this.price.put(Material.SPRUCE_WOOD_STAIRS, 0.28);
		this.price.put(Material.BIRCH_WOOD_STAIRS, 0.28);
		this.price.put(Material.JUNGLE_WOOD_STAIRS, 0.28);
		this.price.put(Material.COMMAND, 0.0);
		// 1 Beacon
		this.price.put(Material.BEACON, 10.0);
		this.price.put(Material.COBBLE_WALL, 0.001);
		this.price.put(Material.FLOWER_POT, 0.001);
		this.price.put(Material.CARROT, 0.0);
		this.price.put(Material.POTATO, 0.0);
		// 333 HolzKnöpfe
		this.price.put(Material.WOOD_BUTTON, 0.18);
		// 3 Köpfe
		this.price.put(Material.SKULL, 150.0);
		// 10 Ambosse
		this.price.put(Material.ANVIL, 15.0);
		// 50 Redstonekisten
		this.price.put(Material.TRAPPED_CHEST, 1.0);
		// 100 Goldplatten
		this.price.put(Material.GOLD_PLATE, 0.01);
		// 100 Eisenplatten
		this.price.put(Material.IRON_PLATE, 3.0);
		this.price.put(Material.REDSTONE_COMPARATOR_OFF, 0.01);
		this.price.put(Material.REDSTONE_COMPARATOR_ON, 0.01);
		this.price.put(Material.DAYLIGHT_DETECTOR, 12.0);
		// 1 Redstoneblock
		this.price.put(Material.REDSTONE_BLOCK, 25.0);
		// 33 Quartzerz
		this.price.put(Material.QUARTZ_ORE, 5.0);
		// 20 Hopper
		this.price.put(Material.HOPPER, 28.0);
		// 70 Quartzblöcke
		this.price.put(Material.QUARTZ_BLOCK, 20.0);
		// 70 Quartzstufen
		this.price.put(Material.QUARTZ_STAIRS, 2.5);
		// 100 Aktivierungsschienen
		this.price.put(Material.ACTIVATOR_RAIL, 0.01);
		// 100 Dropper
		this.price.put(Material.DROPPER, 0.01);
		// ?
		this.price.put(Material.STAINED_CLAY, 0.001);
		// ?
		this.price.put(Material.STAINED_GLASS_PANE, 0.001);
		// 333 Blätter
		this.price.put(Material.LEAVES_2, 0.003);
		// 100 Holz
		this.price.put(Material.LOG_2, 0.01);
		// 300 Holzstufen
		this.price.put(Material.ACACIA_STAIRS, 0.0033);
		this.price.put(Material.DARK_OAK_STAIRS, 0.0033);
		// 100 Heuballen
		this.price.put(Material.HAY_BLOCK, 0.01);
		// 100 Teppich
		this.price.put(Material.CARPET, 0.01);
		// 50 Clay
		this.price.put(Material.HARD_CLAY, 0.02);
		// 5 Kohle
		this.price.put(Material.COAL_BLOCK, 0.2);
		// 10 Packeis
		this.price.put(Material.PACKED_ICE, 0.1);
		// ?
		this.price.put(Material.DOUBLE_PLANT, 0.001);
		this.price.put(Material.IRON_SPADE, 5.0);
		this.price.put(Material.IRON_PICKAXE, 8.33);
		this.price.put(Material.IRON_AXE, 8.33);
		this.price.put(Material.FLINT_AND_STEEL, 3.0);
		this.price.put(Material.APPLE, 10.0);
		this.price.put(Material.BOW, 1.0);
		// 64 Pfeile
		this.price.put(Material.ARROW, 0.015625);
		this.price.put(Material.COAL, 0.0);
		this.price.put(Material.DIAMOND, 5.55);
		this.price.put(Material.IRON_INGOT, 2.77);
		this.price.put(Material.GOLD_INGOT, 3.33);
		this.price.put(Material.IRON_SWORD, 5.0);
		this.price.put(Material.WOOD_SWORD, 0.5);
		this.price.put(Material.WOOD_SPADE, 0.5);
		this.price.put(Material.WOOD_PICKAXE, 0.5);
		this.price.put(Material.WOOD_AXE, 0.5);
		this.price.put(Material.STONE_SWORD, 0.0);
		this.price.put(Material.STONE_SPADE, 0.5);
		this.price.put(Material.STONE_PICKAXE, 0.5);
		this.price.put(Material.STONE_AXE, 0.5);
		this.price.put(Material.DIAMOND_SWORD, 10.0);
		this.price.put(Material.DIAMOND_SPADE, 10.0);
		this.price.put(Material.DIAMOND_PICKAXE, 15.0);
		this.price.put(Material.DIAMOND_AXE, 15.0);
		this.price.put(Material.STICK, 0.005);
		this.price.put(Material.BOWL, 0.007);
		this.price.put(Material.MUSHROOM_SOUP, 1.0);
		this.price.put(Material.GOLD_SWORD, 6.0);
		this.price.put(Material.GOLD_SPADE, 6.0);
		this.price.put(Material.GOLD_PICKAXE, 9.0);
		this.price.put(Material.GOLD_AXE, 10.0);
		this.price.put(Material.STRING, 0.1);
		this.price.put(Material.FEATHER, 0.04);
		this.price.put(Material.SULPHUR, 0.0);
		this.price.put(Material.WOOD_HOE, 0.3);
		this.price.put(Material.STONE_HOE, 0.3);
		this.price.put(Material.IRON_HOE, 5.0);
		this.price.put(Material.DIAMOND_HOE, 10.0);
		this.price.put(Material.GOLD_HOE, 6.0);
		this.price.put(Material.SEEDS, 0.004);
		this.price.put(Material.WHEAT, 0.0);
		this.price.put(Material.BREAD, 0.0);
		this.price.put(Material.LEATHER_HELMET, 8.0);
		this.price.put(Material.LEATHER_CHESTPLATE, 8.0);
		this.price.put(Material.LEATHER_LEGGINGS, 8.0);
		this.price.put(Material.LEATHER_BOOTS, 8.0);
		this.price.put(Material.CHAINMAIL_HELMET, 0.0);
		this.price.put(Material.CHAINMAIL_CHESTPLATE, 0.0);
		this.price.put(Material.CHAINMAIL_LEGGINGS, 0.0);
		this.price.put(Material.CHAINMAIL_BOOTS, 0.0);
		this.price.put(Material.IRON_HELMET, 0.0);
		this.price.put(Material.IRON_CHESTPLATE, 0.0);
		this.price.put(Material.IRON_LEGGINGS, 0.0);
		this.price.put(Material.IRON_BOOTS, 0.0);
		this.price.put(Material.DIAMOND_HELMET, 7.0);
		this.price.put(Material.DIAMOND_CHESTPLATE, 7.0);
		this.price.put(Material.DIAMOND_LEGGINGS, 7.0);
		this.price.put(Material.DIAMOND_BOOTS, 20.0);
		this.price.put(Material.GOLD_HELMET, 10.0);
		this.price.put(Material.GOLD_CHESTPLATE, 10.0);
		this.price.put(Material.GOLD_LEGGINGS, 10.0);
		this.price.put(Material.GOLD_BOOTS, 10.0);
		this.price.put(Material.FLINT, 0.0);
		this.price.put(Material.PORK, 0.0);
		this.price.put(Material.GRILLED_PORK, 0.0);
		this.price.put(Material.PAINTING, 6.0);
		this.price.put(Material.GOLDEN_APPLE, 5.0);
		// ?
		this.price.put(Material.SIGN, 0.01);
		this.price.put(Material.WOOD_DOOR, 0.0);
		this.price.put(Material.BUCKET, 0.2);
		this.price.put(Material.WATER_BUCKET, 0.5);
		// 4 Level ein Eimer
		this.price.put(Material.LAVA_BUCKET, 4.0);
		this.price.put(Material.MINECART, 3.5);
		this.price.put(Material.SADDLE, 20.0);
		this.price.put(Material.IRON_DOOR, 0.0);
		// 100 Redstone
		this.price.put(Material.REDSTONE, 2.77);
		this.price.put(Material.SNOW_BALL, 0.0);
		this.price.put(Material.BOAT, 0.0);
		this.price.put(Material.LEATHER, 3.0);
		this.price.put(Material.MILK_BUCKET, 0.2);
		this.price.put(Material.CLAY_BRICK, 1.2);
		this.price.put(Material.CLAY_BALL, 1.0);
		this.price.put(Material.SUGAR_CANE, 0.15625);
		this.price.put(Material.PAPER, 0.15625);
		this.price.put(Material.BOOK, 0.75);
		this.price.put(Material.SLIME_BALL, 15.0);
		this.price.put(Material.STORAGE_MINECART, 0.0);
		this.price.put(Material.POWERED_MINECART, 0.0);
		this.price.put(Material.EGG, 0.0625);
		this.price.put(Material.COMPASS, 25.0);
		this.price.put(Material.FISHING_ROD, 0.0);
		this.price.put(Material.WATCH, 25.0);
		this.price.put(Material.GLOWSTONE_DUST, 0.875);
		this.price.put(Material.RAW_FISH, 0.0);
		this.price.put(Material.COOKED_FISH, 0.0);
		this.price.put(Material.INK_SACK, 0.0);
		this.price.put(Material.BONE, 0.0);
		this.price.put(Material.SUGAR, 0.0);
		this.price.put(Material.CAKE, 0.0);
		this.price.put(Material.BED, 0.0);
		this.price.put(Material.DIODE, 0.0);
		this.price.put(Material.COOKIE, 0.0);
		this.price.put(Material.MAP, 0.0);
		this.price.put(Material.SHEARS, 0.0);
		this.price.put(Material.MELON, 0.0);
		// ?
		this.price.put(Material.PUMPKIN_SEEDS, 0.001);
		this.price.put(Material.MELON_SEEDS, 0.001);
		this.price.put(Material.RAW_BEEF, 0.0);
		this.price.put(Material.COOKED_BEEF, 0.0);
		this.price.put(Material.RAW_CHICKEN, 0.0);
		this.price.put(Material.COOKED_CHICKEN, 0.0);
		this.price.put(Material.ROTTEN_FLESH, 0.2);
		this.price.put(Material.ENDER_PEARL, 3.5);
		this.price.put(Material.BLAZE_ROD, 20.0);
		this.price.put(Material.GHAST_TEAR, 100.0);
		this.price.put(Material.GOLD_NUGGET, 0.053);
		// ?
		this.price.put(Material.NETHER_STALK, 0.001);
		this.price.put(Material.POTION, 0.33);
		this.price.put(Material.GLASS_BOTTLE, 0.33);
		this.price.put(Material.SPIDER_EYE, 0.0);
		this.price.put(Material.FERMENTED_SPIDER_EYE, 0.0);
		this.price.put(Material.BLAZE_POWDER, 9.0);
		this.price.put(Material.MAGMA_CREAM, 0.0);
		this.price.put(Material.BREWING_STAND_ITEM, 15.0);
		this.price.put(Material.CAULDRON_ITEM, 0.0);
		this.price.put(Material.EYE_OF_ENDER, 0.0);
		this.price.put(Material.SPECKLED_MELON, 0.0);
		this.price.put(Material.MONSTER_EGG, 50.0);
		this.price.put(Material.EXP_BOTTLE, 0.0);
		this.price.put(Material.FIREBALL, 0.0);
		this.price.put(Material.BOOK_AND_QUILL, 0.0);
		this.price.put(Material.WRITTEN_BOOK, 0.0);
		this.price.put(Material.EMERALD, 8.0);
		// 45 ItemFrames
		this.price.put(Material.ITEM_FRAME, 0.02222);
		this.price.put(Material.FLOWER_POT_ITEM, 0.0);
		this.price.put(Material.CARROT_ITEM, 0.0);
		this.price.put(Material.POTATO_ITEM, 0.0);
		this.price.put(Material.BAKED_POTATO, 0.0);
		this.price.put(Material.POISONOUS_POTATO, 0.0);
		this.price.put(Material.EMPTY_MAP, 0.0);
		this.price.put(Material.GOLDEN_CARROT, 0.0);
		this.price.put(Material.SKULL_ITEM, 0.0);
		this.price.put(Material.CARROT_STICK, 0.0);
		this.price.put(Material.NETHER_STAR, 0.0);
		this.price.put(Material.PUMPKIN_PIE, 0.0);
		this.price.put(Material.FIREWORK, 0.0);
		this.price.put(Material.FIREWORK_CHARGE, 0.0);
		this.price.put(Material.ENCHANTED_BOOK, 0.0);
		this.price.put(Material.REDSTONE_COMPARATOR, 0.0);
		this.price.put(Material.NETHER_BRICK_ITEM, 0.0);
		// 60 Quartz
		this.price.put(Material.QUARTZ, 0.01666);
		this.price.put(Material.EXPLOSIVE_MINECART, 0.0);
		this.price.put(Material.HOPPER_MINECART, 0.01);
		this.price.put(Material.IRON_BARDING, 0.01);
		this.price.put(Material.GOLD_BARDING, 0.01);
		this.price.put(Material.DIAMOND_BARDING, 0.01);
		this.price.put(Material.LEASH, 0.0);
		this.price.put(Material.NAME_TAG, 0.0);
		this.price.put(Material.COMMAND_MINECART, 1.0);
		this.price.put(Material.GOLD_RECORD, 0.0);
		this.price.put(Material.GREEN_RECORD, 0.0);
		this.price.put(Material.RECORD_3, 100.0);
		this.price.put(Material.RECORD_4, 100.0);
		this.price.put(Material.RECORD_5, 100.0);
		this.price.put(Material.RECORD_6, 100.0);
		this.price.put(Material.RECORD_7, 100.0);
		this.price.put(Material.RECORD_8, 100.0);
		this.price.put(Material.RECORD_9, 100.0);
		this.price.put(Material.RECORD_10, 100.0);
		this.price.put(Material.RECORD_11, 100.0);
		this.price.put(Material.RECORD_12, 100.0);
	}

	public double getLevelForItems(ItemStack[] itemStacks) {
		double d = 0D;
		for (ItemStack item : itemStacks)
			if (item != null && !item.getType().equals(Material.AIR))
				d += this.price.get(item.getType()) * item.getAmount();
		return d;
	}
}
