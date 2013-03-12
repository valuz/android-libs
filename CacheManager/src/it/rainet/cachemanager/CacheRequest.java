package it.rainet.cachemanager;

public class CacheRequest {

	private String url;
	private String extra;

	public CacheRequest(String url, String extra) {
		this.url = url;
		this.extra = extra;
	}
	
	public String getUrl() {
		return url;
	}

	public String getExtra() {
		return extra;
	}

}
