package com.sourceallies.space.ship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sourceallies.space.ChatMessage;

import cloud.orbit.actors.ObserverManager;
import cloud.orbit.actors.runtime.AbstractActor;
import cloud.orbit.actors.streams.AsyncStream;
import cloud.orbit.actors.streams.StreamSequenceToken;
import cloud.orbit.actors.streams.StreamSubscriptionHandle;
import cloud.orbit.concurrent.Task;

public class SpaceshipActorImpl 
	extends AbstractActor<SpaceshipActorImpl.SpaceshipState> 
	implements SpaceshipActor {
	
	private ObserverManager<SpaceshipObserver> observers = new ObserverManager<>();
	private StreamSubscriptionHandle<ChatMessage> areaChatStreamHandle;
	
	@Override
	public Task<Void> setObserver(SpaceshipObserver observer) {
		getLogger().info("Setting observer {}", observer);
		observers.clear();
		observers.addObserver(observer);
		return Task.done();
	}
	
	@Override
	public Task<?> activateAsync() {
		getLogger().info("Activating [{}]", getIdentity());
		subscribeToAreaChat();
		return super.activateAsync();
	}
	
	@Override
	public Task<String> getAreaChatStreamId() {
		return Task.fromValue(getAreaChatStreamIdInternal());
	}
	
	private void subscribeToAreaChat() {
		AsyncStream<ChatMessage> areaChat = AsyncStream.getStream(ChatMessage.class, getAreaChatStreamIdInternal());
		areaChatStreamHandle = areaChat.subscribe((ChatMessage message, StreamSequenceToken token) -> {
			getLogger().info("Got area chat {}", message);
			observers.notifyObservers(observable -> observable.onChatMessageReceived(message));
			return Task.done();
		}).join();
	}
	
	private String getAreaChatStreamIdInternal() {
		return "/area/" + state().getLocation() + "/chat";
	}
	
	public static class SpaceshipState {
		
		private String location = "Eastern Front";
		
		public String getLocation() {
			return location;
		}
	}	
}
