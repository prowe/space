package com.sourceallies.space;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Controller
public class ChatController implements ApplicationListener<SessionSubscribeEvent>{
	private static Logger logger = LoggerFactory.getLogger(ChatController.class);
		
	@Autowired
	private SimpMessageSendingOperations messageSender;
	
	@MessageMapping("/command")
	public void handleChatMessage(ChatMessage message, Principal user) {
		message.setSourceId(user.getName());
		logger.info("Handling chat message: {}", message);
		messageSender.convertAndSendToUser(user.getName(), "/topic/messages", message);
	}

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		logger.info("subscribing: {}", event);
	}
}
