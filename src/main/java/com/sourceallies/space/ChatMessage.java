package com.sourceallies.space;

public class ChatMessage {

	private String sourceId;
	private String message;

	@Override
	public String toString() {
		return "ChatMessage [sourceId=" + sourceId + ", message=" + message + "]";
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
