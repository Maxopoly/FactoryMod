package com.github.igotyou.FactoryMod.repairManager;

public interface IRepairManager {
	public void breakIt();
	
	public String getHealth();

	public boolean atFullHealth();

	public void repair(int amount);

	public boolean inDisrepair();

}