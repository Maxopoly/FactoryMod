package com.github.igotyou.FactoryMod.utility;

public class MultiplierConfig {
	
	private int upperRunCap;
	private double outerBonusMultiplier;
	private double innerBonusMultiplier;
	private double additionalBonusConstant;
	
	public MultiplierConfig(int upperRunCap, double outerBonusMultiplier, double innerBonusMultiplier, double additionalBonusConstant) {
		this.upperRunCap = upperRunCap;
		this.outerBonusMultiplier = outerBonusMultiplier;
		this.innerBonusMultiplier = innerBonusMultiplier;
		this.additionalBonusConstant = additionalBonusConstant;
	}
	
	public double getMultiplier(int run) {
		int actualRun = upperRunCap != -1 ? Math.max(run, upperRunCap) : run;
		return (outerBonusMultiplier * Math.log(innerBonusMultiplier * actualRun)) + additionalBonusConstant;
	}
}
