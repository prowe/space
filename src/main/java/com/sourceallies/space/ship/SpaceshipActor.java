package com.sourceallies.space.ship;

import cloud.orbit.actors.Actor;
import cloud.orbit.concurrent.Task;

public interface SpaceshipActor extends Actor{

	public Task<Void> setObserver(SpaceshipObserver observer);
	
	public Task<String> getAreaChatStreamId();
}
