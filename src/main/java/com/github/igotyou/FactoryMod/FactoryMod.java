package com.github.igotyou.FactoryMod;

import org.bukkit.entity.Player;

import com.github.igotyou.FactoryMod.commands.FactoryModCommandHandler;
import com.github.igotyou.FactoryMod.listeners.CompactItemListener;
import com.github.igotyou.FactoryMod.listeners.FactoryModListener;
import com.github.igotyou.FactoryMod.structures.MultiBlockStructure;
import com.github.igotyou.FactoryMod.utility.MenuBuilder;

import vg.civcraft.mc.civmenu.guides.ResponseManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class FactoryMod extends ACivMod {
	private FactoryModManager manager;
	private static FactoryMod plugin;
	private MenuBuilder mb;
	private ResponseManager rm;

	public void onEnable() {
		handle = new FactoryModCommandHandler();
		handle.registerCommands();
		super.onEnable();
		plugin = this;
		MultiBlockStructure.initializeBlockFaceMap();
		ConfigParser cp = new ConfigParser(this);
		manager = cp.parse();
		mb = new MenuBuilder(cp.getDefaultMenuFactory());
		manager.loadFactories();
		registerListeners();
		if (getServer().getPluginManager().isPluginEnabled("CivMenu")) {
			rm = ResponseManager.getResponseManager(this);
		}
		info("Successfully enabled");
	}

	public void onDisable() {
		manager.shutDown();
		plugin.info("Shutting down");
	}

	public static FactoryModManager getManager() {
		return getInstance().manager;
	}

	public String getPluginName() {
		return "FactoryMod";
	}

	public static FactoryMod getInstance() {
		return plugin;
	}

	private void registerListeners() {
		plugin.getServer().getPluginManager()
				.registerEvents(new FactoryModListener(manager), plugin);
		plugin.getServer()
				.getPluginManager()
				.registerEvents(
						new CompactItemListener(manager.getCompactLore()),
						plugin);
	}

	public static MenuBuilder getMenuBuilder() {
		return getInstance().mb;
	}

	/**
	 * Sends a CivMenu response
	 */
	public static void sendResponse(String event, Player p) {
		if (getInstance().rm != null) {
			getInstance().rm.sendMessageForEvent(event, p);
		}
	}
}
