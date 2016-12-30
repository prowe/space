package com.sourceallies.space.ship;

public class Attack {

	private String sourceId;
	private String targetId;
	
	@Override
	public String toString() {
		return "Attack[" + sourceId + "->" + targetId + "]";
	}
	public String getSourceId() {
		return sourceId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}
