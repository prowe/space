package com.sourceallies.space.ship;

import cloud.orbit.actors.Actor;
import cloud.orbit.concurrent.Task;

public interface SpaceshipActor extends Actor{

	public Task<Void> setObserver(SpaceshipObserver observer);
	
	public Task<String> getAreaEventStreamId();
	
	//TODO: make generic to an "action"
	public Task<AttackResult> performAttack(Attack attack);
	
	public Task<AttackResult> beAttacked(Attack attack);
}
