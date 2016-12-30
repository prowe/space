package com.sourceallies.space;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.sourceallies.space.ship.Attack;
import com.sourceallies.space.ship.AttackResult;
import com.sourceallies.space.ship.SpaceshipActor;
import com.sourceallies.space.ship.SpaceshipObserver;

import cloud.orbit.actors.Actor;
import cloud.orbit.actors.streams.AsyncStream;
import cloud.orbit.concurrent.Task;

@Controller
public class ClientMessageController implements ApplicationListener<SessionSubscribeEvent>{
	private static Logger logger = LoggerFactory.getLogger(ClientMessageController.class);
		
	@Autowired
	private SimpMessageSendingOperations messageSender;
	
	@MessageMapping("/chat-messages")
	public void handleChatMessage(ChatMessage message, Principal principal) {
		message.setSourceId(principal.getName());
		logger.info("Handling chat message: {}", message);

		String areaChatStreamId = getSpaceshipForPrincipal(principal)
			.getAreaEventStreamId()
			.join();
		
		AsyncStream.getStream(ChatMessage.class, areaChatStreamId)
			.publish(message)
			.join();
	}
	
	@MessageMapping("/perform-attack")
	public void performAttack(Attack attack, Principal principal) {
		logger.info("Handling perform attack: {}", attack);
		getSpaceshipForPrincipal(principal)
			.performAttack(attack)
			.join();
	}
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		logger.info("subscribing: {}", event);
		getSpaceshipForPrincipal(event.getUser())
			.setObserver(new WebsocketSpaceshipObserver(event.getUser().getName()))
			.join();
	}
	
	private SpaceshipActor getSpaceshipForPrincipal(Principal principal) {
		return Actor.getReference(SpaceshipActor.class, principal.getName());
	}
	
	private class WebsocketSpaceshipObserver implements SpaceshipObserver {
		private final String user;
		
		public WebsocketSpaceshipObserver(String user) {
			this.user = user;
		}
		
		@Override
		public Task<Void> onChatMessageReceived(ChatMessage message) {
			logger.info("Forwarding message to user: {}", message);
			messageSender.convertAndSendToUser(user, "/topic/chat", message);
			return Task.done();
		}

		@Override
		public Task<Void> onAreaAction(AttackResult result) {
			logger.info("Forwarding message to user: {}", result);
			messageSender.convertAndSendToUser(user, "/topic/actions", result);
			return Task.done();
		}
	}
}
