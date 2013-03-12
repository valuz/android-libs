package it.rainet.cachemanager.network.listener;

import it.rainet.cachemanager.network.CacheResponse;

public interface CacheResponseListener {

	public void onCachedResponseReceived(CacheResponse response);

	public void onCacheError(Exception exception, String extra);
}
