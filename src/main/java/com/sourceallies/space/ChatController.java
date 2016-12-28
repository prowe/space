package com.sourceallies.space;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import cloud.orbit.actors.streams.AsyncStream;
import cloud.orbit.actors.streams.StreamSequenceToken;
import cloud.orbit.actors.streams.StreamSubscriptionHandle;
import cloud.orbit.concurrent.Task;

@Controller
public class ChatController implements ApplicationListener<SessionSubscribeEvent>{
	private static Logger logger = LoggerFactory.getLogger(ChatController.class);
		
	@Autowired
	private SimpMessageSendingOperations messageSender;
	private Map<String, StreamToTopicForwarder> forwardersBySessionId = new HashMap<>();
	
	@MessageMapping("/command")
	public void handleChatMessage(ChatMessage message, Principal principal) {
		message.setSourceId(principal.getName());
		logger.info("Handling chat message: {}", message);
		//messageSender.convertAndSendToUser(user.getName(), "/topic/messages", message);
	
		getStreamForPrincipal(principal)
			.publish(message);
	}
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		logger.info("subscribing: {}", event);
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
		StreamToTopicForwarder forwarder = new StreamToTopicForwarder(event.getUser());
		forwarder.subscribe();
		forwardersBySessionId.put(headers.getSessionId(), forwarder);
	}
	
	private AsyncStream<ChatMessage> getStreamForPrincipal(Principal principal) {
		return AsyncStream.getStream(ChatMessage.class, "/user/" + principal.getName() + "/messages");
	}
	
	private class StreamToTopicForwarder {
		
		private StreamSubscriptionHandle<ChatMessage> handle;
		private final Principal principal;

		public StreamToTopicForwarder(Principal principal) {
			this.principal = principal;
		}
		
		public void subscribe() {
			try {
				handle = getStreamForPrincipal(principal)
					.subscribe(this::onNext)
					.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Error subscribing", e);
				throw new RuntimeException(e);
			}
		}
		
		private Task<Void> onNext(ChatMessage message, final StreamSequenceToken sequenceToken) {
			logger.info("Forwarding message to user");
			messageSender.convertAndSendToUser(principal.getName(), "/topic/messages", message);
			return Task.done();
		}
	}
}
