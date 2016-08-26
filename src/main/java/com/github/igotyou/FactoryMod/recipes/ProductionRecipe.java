package com.github.igotyou.FactoryMod.recipes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

import com.github.igotyou.FactoryMod.factories.FurnCraftChestFactory;
import com.github.igotyou.FactoryMod.utility.MultiplierConfig;

/**
 * Consumes a set of materials from a container and outputs another set of
 * materials to the same container
 *
 */
public class ProductionRecipe extends InputRecipe {
	
	private ItemMap output;
	private TreeMap <Integer, MultiplierConfig> boni;
	private DecimalFormat decimalFormat;

	public ProductionRecipe(String identifier, String name, int productionTime, ItemMap inputs, int uses,
			ItemMap output, TreeMap <Integer, MultiplierConfig> boni) {
		super(identifier, name, productionTime, inputs, uses);
		this.output = output;
		this.boni = boni;
		this.decimalFormat = new DecimalFormat("#,#####");
	}

	public ItemMap getOutput() {
		return output;
	}

	public List<ItemStack> getOutputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		List<ItemStack> stacks;
		if (i == null || fccf == null) {
			stacks = output.getItemStackRepresentation();
			return stacks;
		}
		stacks = getAdjustedOutput(fccf).getItemStackRepresentation();
		int possibleRuns = input.getMultiplesContainedIn(i);
		for (ItemStack is : stacks) {
			ISUtils.addLore(is, ChatColor.GREEN + "Enough materials for "
					+ String.valueOf(possibleRuns) + " runs");
		}
		return stacks;
	}

	public List<ItemStack> getInputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		if (i == null) {
			return input.getItemStackRepresentation();
		}
		return createLoredStacksForInfo(i);
	}

	public void applyEffect(Inventory i, FurnCraftChestFactory f) {
		logBeforeRecipeRun(i, f);
		ItemMap toAdd = getAdjustedOutput(f);
		if (input.isContainedIn(i)) {
			if (input.removeSafelyFrom(i)) {
				for(ItemStack is: toAdd.getItemStackRepresentation()) {
					i.addItem(is);
				}
			}
		}
		logAfterRecipeRun(i, f);
	}
	
	public ItemMap getAdjustedOutput(FurnCraftChestFactory fccf) {
		ItemMap toAdd = output.clone();
		toAdd.multiplyContent(getMultiplier(fccf.getRunCount(this)));
		return toAdd;
	}

	public ItemStack getRecipeRepresentation(FurnCraftChestFactory fccf) {
		List<ItemStack> out = output.getItemStackRepresentation();
		ItemStack res;
		if (out.size() == 0) {
			res = new ItemStack(Material.STONE);
		} else {
			res = out.get(0);
		}
		ISUtils.setName(res, getName());
		if (fccf == null) {
			return res;
		}
		double multi = getMultiplier(fccf.getRunCount(this));
		if (multi != 1.0) {
			ISUtils.addLore(res, ChatColor.GOLD + "Current multiplier: " + decimalFormat.format(multi));
		}
		return res;
	}
	
	public double getMultiplier(int run) {
		Entry <Integer, MultiplierConfig> entry = boni.floorEntry(run);
		if (entry == null) {
			return 1.0;
		}
		return entry.getValue().getMultiplier(run);
		
	}
}
