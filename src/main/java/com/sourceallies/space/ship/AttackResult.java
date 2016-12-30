package com.sourceallies.space.ship;

public class AttackResult {

	private final Attack attack;
	private final int damageDelt;
	private final int targetHealthPercent;
	
	public AttackResult(Attack attack, int damageDelt, int targetHealthPercent) {
		this.attack = attack;
		this.damageDelt = damageDelt;
		this.targetHealthPercent = targetHealthPercent;
	}
	
	public Attack getAttack() {
		return attack;
	}
	public int getDamageDelt() {
		return damageDelt;
	}
	public int getTargetHealthPercent() {
		return targetHealthPercent;
	}
}
