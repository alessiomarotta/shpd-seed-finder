package com.shatteredpixel.shatteredpixeldungeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;

public class SeedFinder {
	List<Class<? extends Item>> blacklist;

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

    public SeedFinder() {
		int floors = 1;

		for (int i = 0; i < DungeonSeed.TOTAL_SEEDS; i++) {
			if (testSeed(Integer.toString(i), floors)) {
				listSeedItems(Integer.toString(i), 4);
			}
		}
	}

	private boolean testSeed(String seed, int floors) {
		SPDSettings.customSeed(seed);
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		// TODO: check animated statues and mimic drops
		for (int i = 0; i < floors; i++) {
			Level l = Dungeon.newLevel();
			List<Heap> heaps = l.heaps.valueList();

			for (Heap h : heaps) {
				Item item = h.peek();
				item.identify();

				// put your constraints here
				if (item instanceof WandOfDisintegration && item.level() == 2)
					return true;
			}

			Dungeon.depth++;
		}

		return false;
	}

	private void listSeedItems(String seed, int floors) {
		SPDSettings.customSeed(seed);
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		blacklist = Arrays.asList(Gold.class, Dewdrop.class, IronKey.class, GoldenKey.class, CrystalKey.class, EnergyCrystal.class,
								  CorpseDust.class, Embers.class, CeremonialCandle.class, Pickaxe.class);

		System.out.printf("Items for seed %s (%d):\n", DungeonSeed.convertToCode(Dungeon.seed), Dungeon.seed);

		for (int i = 0; i < floors; i++) {
			System.out.println("--- Floor " + Dungeon.depth + " ---\n");

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

			// addTextItems("Scrolls", scrolls, builder);
			// addTextItems("Potions", potions, builder);
			addTextItems("Equipment", equipment, builder);
			addTextItems("Rings", rings, builder);
			addTextItems("Artifacts", artifacts, builder);
			addTextItems("Wands", wands, builder);
			// addTextItems("Other", others, builder);

			if (builder.length() > 0)
				builder.setLength(builder.length()-1);

			System.out.println(builder.toString());

			Dungeon.depth++;
		}
    }
}
