package com.shatteredpixel.shatteredpixeldungeon;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;

public class SeedFinder {
	enum Condition {ANY, ALL};

	public static class Options {
		public static int floors;
		public static Condition condition;
		public static String itemListFile;
		public static String ouputFile;
	}

	List<Class<? extends Item>> blacklist;
	ArrayList<String> itemList;

	// TODO: make it parse the item list directly from the arguments
	private void parseArgs(String[] args) {
		Options.floors = Integer.parseInt(args[0]);
		Options.condition = args[1].equals("any") ? Condition.ANY : Condition.ALL;
		Options.itemListFile = args[2];

		if (args.length < 4)
			Options.ouputFile = "out.txt";

		else
			Options.ouputFile = args[3];
	}

	private ArrayList<String> getItemList() {
		ArrayList<String> itemList = new ArrayList<>();

		try {
			Scanner scanner = new Scanner(new File(Options.itemListFile));

			while (scanner.hasNextLine()) {
				itemList.add(scanner.nextLine());
			}

			scanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return itemList;
	}

	private void addTextItems(String caption, ArrayList<Heap> items, StringBuilder builder) {
		if (!items.isEmpty()) {
			builder.append(caption + ":\n");

			for (Heap h : items) {
				Item i = h.peek();

				if (((i instanceof Armor && ((Armor) i).hasGoodGlyph()) ||
					 (i instanceof Weapon && ((Weapon) i).hasGoodEnchant()) ||
					 (i instanceof Ring)) && i.cursed)
					builder.append("- cursed " + i.toString().toLowerCase());

				else
					builder.append("- " + i.toString().toLowerCase());

				if (h.type != Type.HEAP)
					builder.append(" (" + h.toString().toLowerCase() + ")");

				builder.append("\n");
			}

			builder.append("\n");
		}
	}

	private void addTextQuest(String caption, ArrayList<Item> items, StringBuilder builder) {
		if (!items.isEmpty()) {
			builder.append(caption + ":\n");

			for (Item i : items) {
				if (i.cursed)
					builder.append("- cursed " + i.toString().toLowerCase() + "\n");

				else
					builder.append("- " + i.toString().toLowerCase() + "\n");
			}

			builder.append("\n");
		}
	}

    public SeedFinder(String[] args) {
		parseArgs(args);
		itemList = getItemList();

		for (int i = 0; i < DungeonSeed.TOTAL_SEEDS; i++) {
			if (testSeed(Integer.toString(i), Options.floors)) {
				System.out.printf("Found valid seed %s (%d)\n", DungeonSeed.convertToCode(Dungeon.seed), Dungeon.seed);
				logSeedItems(Integer.toString(i), Options.floors);
			}
		}
	}

	private boolean testSeed(String seed, int floors) {
		SPDSettings.customSeed(seed);
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		int itemsFound = 0;

		// TODO: check animated statues and mimic drops
		for (int i = 0; i < floors; i++) {
			Level l = Dungeon.newLevel();
			List<Heap> heaps = l.heaps.valueList();

			for (Heap h : heaps) {
				Item item = h.peek();
				item.identify();

				for (String c : itemList) {
					if (item.toString().toLowerCase().contains(c))
						itemsFound += 1;
				}
			}

			Dungeon.depth++;
		}

		if (Options.condition == Condition.ANY)
			return itemsFound > 0;

		else
			return itemsFound == itemList.size();
	}

	private void logSeedItems(String seed, int floors) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileOutputStream(Options.ouputFile, true));
		} catch (FileNotFoundException e) { // gotta love Java mandatory exceptions
			e.printStackTrace();
		}

		SPDSettings.customSeed(seed);
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		blacklist = Arrays.asList(Gold.class, Dewdrop.class, IronKey.class, GoldenKey.class, CrystalKey.class, EnergyCrystal.class,
								  CorpseDust.class, Embers.class, CeremonialCandle.class, Pickaxe.class);

		out.printf("Items for seed %s (%d):\n\n", DungeonSeed.convertToCode(Dungeon.seed), Dungeon.seed);

		for (int i = 0; i < floors; i++) {
			out.printf("--- floor %d ---\n\n", Dungeon.depth);

			Level l = Dungeon.newLevel();
			List<Heap> heaps = l.heaps.valueList();
			StringBuilder builder = new StringBuilder();
			ArrayList<Heap> scrolls = new ArrayList<>();
			ArrayList<Heap> potions = new ArrayList<>();
			ArrayList<Heap> equipment = new ArrayList<>();
			ArrayList<Heap> rings = new ArrayList<>();
			ArrayList<Heap> artifacts = new ArrayList<>();
			ArrayList<Heap> wands = new ArrayList<>();
			ArrayList<Heap> others = new ArrayList<>();

			// list quest rewards
			if (Ghost.Quest.armor != null) {
				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Ghost.Quest.armor);
				rewards.add(Ghost.Quest.weapon);
				Ghost.Quest.complete();

				addTextQuest("Ghost quest rewards", rewards, builder);
			}

			if (Wandmaker.Quest.wand1 != null) {
				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Wandmaker.Quest.wand1);
				rewards.add(Wandmaker.Quest.wand2);
				Wandmaker.Quest.complete();

				builder.append("Wandmaker quest item: ");

				switch (Wandmaker.Quest.type) {
					case 1: default:
						builder.append("corpse dust\n\n");
						break;
					case 2:
						builder.append("fresh embers\n\n");
						break;
					case 3:
						builder.append("rotberry seed\n\n");
				}

				addTextQuest("Wandmaker quest rewards", rewards, builder);
			}

			if (Imp.Quest.reward != null) {
				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Imp.Quest.reward.identify());
				Imp.Quest.complete();

				addTextQuest("Imp quest reward", rewards, builder);
			}

			// list items
			for (Heap h : heaps) {
				Item item = h.peek();
				item.identify();

				if (h.type == Heap.Type.FOR_SALE) continue;
				else if (blacklist.contains(item.getClass())) continue;
				else if (item instanceof Scroll) scrolls.add(h);
				else if (item instanceof Potion) potions.add(h);
				else if (item instanceof MeleeWeapon || item instanceof Armor) equipment.add(h);
				else if (item instanceof Ring) rings.add(h);
				else if (item instanceof Artifact) artifacts.add( h);
				else if (item instanceof Wand) wands.add(h);
				else others.add(h);
			}

			addTextItems("Scrolls", scrolls, builder);
			addTextItems("Potions", potions, builder);
			addTextItems("Equipment", equipment, builder);
			addTextItems("Rings", rings, builder);
			addTextItems("Artifacts", artifacts, builder);
			addTextItems("Wands", wands, builder);
			addTextItems("Other", others, builder);

			out.print(builder.toString());

			Dungeon.depth++;
		}

		out.close();
    }

}
