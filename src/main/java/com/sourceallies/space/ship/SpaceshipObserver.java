package com.sourceallies.space.ship;

import com.sourceallies.space.ChatMessage;

import cloud.orbit.actors.ActorObserver;
import cloud.orbit.actors.annotation.OneWay;
import cloud.orbit.concurrent.Task;

public interface SpaceshipObserver extends ActorObserver{

	@OneWay
	public Task<Void> onChatMessageReceived(ChatMessage message);

	@OneWay
	public Task<Void> onAreaAction(AttackResult result);
}
