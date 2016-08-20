package com.github.igotyou.FactoryMod.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

import com.github.igotyou.FactoryMod.FactoryMod;
import com.github.igotyou.FactoryMod.factories.Factory;
import com.github.igotyou.FactoryMod.factories.FurnCraftChestFactory;

public class RecipeModificationRecipe extends InputRecipe {

	private List<IRecipe> removedRecipes;
	private List<IRecipe> addedRecipes;

	public RecipeModificationRecipe(String identifier, String name, int productionTime, ItemMap inputs, int uses,
			List<IRecipe> removedRecipes, List<IRecipe> addedRecipes) {
		super(identifier, name, productionTime, inputs, uses);
		this.removedRecipes = removedRecipes;
	}

	public List<ItemStack> getOutputRepresentation(Inventory i) {
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		ItemStack is = new ItemStack(Material.BOOK);
		ISUtils.addLore(is, ChatColor.GOLD + "Discovers recipe" + (addedRecipes.size() == 1 ? "" : "s") + ":");
		for (IRecipe rec : addedRecipes) {
			ISUtils.addLore(is, ChatColor.AQUA + rec.getName());
		}
		if (removedRecipes.size() != 0) {
			ISUtils.addLore(is, ChatColor.RED + "Removes recipe" + (addedRecipes.size() == 1 ? "" : "s") + ":");
			for (IRecipe rec : removedRecipes) {
				ISUtils.addLore(is, ChatColor.AQUA + rec.getName());
			}
		}
		stacks.add(is);
		if (i == null) {
			return stacks;
		}
		int possibleRuns = input.getMultiplesContainedIn(i);
		ISUtils.addLore(is, ChatColor.GREEN + "Enough materials for " + String.valueOf(possibleRuns) + " runs");
		return stacks;
	}

	public List<ItemStack> getInputRepresentation(Inventory i) {
		if (i == null) {
			return input.getItemStackRepresentation();
		}
		return createLoredStacksForInfo(i);
	}

	public void applyEffect(Inventory i, Factory f) {
		logBeforeRecipeRun(i, f);
		FurnCraftChestFactory fccf = (FurnCraftChestFactory) f;
		if (input.isContainedIn(i)) {
			if (input.removeSafelyFrom(i)) {
				for (IRecipe rem : removedRecipes) {
					if (!fccf.removeRecipe(rem)) {
						FactoryMod.getInstance().warning(
								"Attempted to remove recipe " + rem.getIdentifier() + " from factory at "
										+ f.getMultiBlockStructure().getCenter().toString()
										+ ", but the factory didn't have it");
					}
				}
				for (IRecipe rem : addedRecipes) {
					if (!fccf.addRecipe(rem)) {
						FactoryMod.getInstance().warning(
								"Attempted to add recipe " + rem.getIdentifier() + " to factory at "
										+ f.getMultiBlockStructure().getCenter().toString()
										+ ", but the factory already had it");
					}
				}
			}
		}
		logAfterRecipeRun(i, f);
	}

	public ItemStack getRecipeRepresentation() {
		ItemStack res = getOutputRepresentation(null).get(0);
		ISUtils.setName(res, getName());
		return res;
	}

	public List<IRecipe> getRemoveRecipes() {
		return removedRecipes;
	}

	public List<IRecipe> getAddedRecipes() {
		return addedRecipes;
	}

}
