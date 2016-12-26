package com.sourceallies.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
	private static Logger logger = LoggerFactory.getLogger(ChatController.class);
		
	@MessageMapping("/command")
	public void handleChatMessage(ChatMessage message) {
		logger.info("Handling chat message: {}", message);
		
	}
}
