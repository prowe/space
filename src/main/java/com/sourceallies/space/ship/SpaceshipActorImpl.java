package com.sourceallies.space.ship;

import java.util.concurrent.ThreadLocalRandom;

import com.sourceallies.space.ChatMessage;

import cloud.orbit.actors.Actor;
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
	private AsyncStream<ChatMessage> areaChatStream;
	private StreamSubscriptionHandle<ChatMessage> areaChatStreamHandle;
	private AsyncStream<AttackResult> areaActionStream;
	
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
		subscribeToAreaActions();
		return super.activateAsync();
	}
	
	@Override
	public Task<String> getAreaEventStreamId() {
		return Task.fromValue(getAreaEventStreamIdInternal());
	}
	
	@Override
	public Task<AttackResult> performAttack(Attack attack) {
		attack.setSourceId(getIdentity());
		getLogger().info("performing attack: {}", attack);
		return Actor.getReference(SpaceshipActor.class, attack.getTargetId())
			.beAttacked(attack)
			.thenApply(result -> {
				areaActionStream.publish(result);
				return result;
			});
	}
	
	//FIXME:
	@Override
	public Task<AttackResult> beAttacked(Attack attack) {
		getLogger().info("being attacked: {}", attack);
		ThreadLocalRandom random = ThreadLocalRandom.current();
		boolean hit = random.nextBoolean();
		int damage = hit ? (random.nextInt(10) + 1) : 0;
		state().takeDamage(damage);
		return Task.fromValue(new AttackResult(attack, damage, state().getHealthPercent()));
	}
	
	private void subscribeToAreaChat() {
		areaChatStream = AsyncStream.getStream(ChatMessage.class, getAreaEventStreamIdInternal());
		areaChatStreamHandle = areaChatStream.subscribe(this::onChatMessageReceived).join();
	}
	
	private Task<Void> onChatMessageReceived(ChatMessage message, StreamSequenceToken token) {
		getLogger().info("Got chat message {}", message);
		observers.notifyObservers(observable -> observable.onChatMessageReceived(message));
		return Task.done();
	}
	
	private void subscribeToAreaActions() {
		areaActionStream = AsyncStream.getStream(AttackResult.class, "/area/" + state.getLocation() + "/actions");
		areaActionStream.subscribe(this::onAreaAction)
			.join();
	}
	
	private Task<Void> onAreaAction(AttackResult result, StreamSequenceToken token) {
		getLogger().info("Got area action: {}", result);
		observers.notifyObservers(o -> o.onAreaAction(result));
		return Task.done();
	}
	
	private String getAreaEventStreamIdInternal() {
		return "/area/" + state().getLocation() + "/events";
	}
	
	public static class SpaceshipState {
		
		private String location = "Eastern Front";
		private int totalHealth = 1000;
		private int damageTaken = 0;
		
		private void takeDamage(int damage) {
			damageTaken+=damage;
		}
		public int getHealthPercent() {
			if(damageTaken == 0) {
				return 100;
			}
			if(damageTaken >= totalHealth) {
				return 0;
			}
			return 100 - (100 * damageTaken)/(100 * totalHealth);
		}
		public String getLocation() {
			return location;
		}
		public int getTotalHealth() {
			return totalHealth;
		}
		public int getDamageTaken() {
			return damageTaken;
		}
	}	
}
