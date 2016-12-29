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
			.getAreaChatStreamId()
			.join();
		
		AsyncStream.getStream(ChatMessage.class, areaChatStreamId)
			.publish(message)
			.join();
	}
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		logger.info("subscribing: {}", event);
		SpaceshipObserver observer = new SpaceshipObserver() {
			@Override
			public Task<Void> onChatMessageReceived(ChatMessage message) {
				logger.info("Forwarding message to user: {}", message);
				messageSender.convertAndSendToUser(event.getUser().getName(), "/topic/chat", message);
				return Task.done();
			}
		};
		getSpaceshipForPrincipal(event.getUser())
			.setObserver(observer)
			.join();
	}
	
	private SpaceshipActor getSpaceshipForPrincipal(Principal principal) {
		return Actor.getReference(SpaceshipActor.class, principal.getName());
	}
}