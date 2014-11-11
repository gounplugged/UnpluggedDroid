package es.theedg.hydra;

import java.util.UUID;

public class HydraPost {
	private final String id;
	private final long timestamp;
	private final String content;
	
	public HydraPost(String id_, long timestamp_, String content_) {
		this.id = id_;
		this.timestamp = timestamp_;
		this.content = content_;
	}
	
	public HydraPost(String content_) {
		this.content = content_;
		this.timestamp = System.currentTimeMillis();
		this.id = (UUID.randomUUID()).toString();
	}

	public String getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getContent() {
		return content;
	}

}
