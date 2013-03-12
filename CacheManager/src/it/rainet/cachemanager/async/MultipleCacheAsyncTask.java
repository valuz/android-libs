package it.rainet.cachemanager.async;

import it.rainet.cachemanager.CacheRequest;
import it.rainet.cachemanager.cache.DiskCache;
import it.rainet.cachemanager.cache.DiskCache.DiskReadException;
import it.rainet.cachemanager.network.CacheResponse;
import it.rainet.cachemanager.network.listener.CacheResponseListener;

import org.apache.http.HttpResponse;

public class MultipleCacheAsyncTask extends CacheAsyncTask {

	public MultipleCacheAsyncTask(DiskCache diskCache, CacheResponseListener listener) {
		super(diskCache, listener);
	}

	@Override
	protected Boolean doInBackground(CacheRequest... requests) {
		if (requests.length <= 0) {
			exception = new RuntimeException("No Request found");
			extra = "";
			return false;
		}
		for (CacheRequest request : requests) {
			String url = request.getUrl();
			if (url == null || url.length() == 0) {
				RuntimeException exception = new RuntimeException("Url cannot be empty");
				publishProgress(exception, request.getExtra());
			} else {
				try {
					if (diskCache.isCached(url)) {
						CacheResponse cachedData = diskCache.getCachedData(url);
						HttpResponse headWebData = headWebData(url);
						String headContentLenght = headWebData.getFirstHeader("Content-Length").getValue();
						String cacheContentLenght = cachedData.getHeader("Content-Length");
						if (headContentLenght.equals(cacheContentLenght)) {
							cachedData.setTag(CacheResponse.CACHED);
							cachedData.setExtra(request.getExtra());
							publishProgress(cachedData);
						} else {
							HttpResponse httpResponse = getWebData(url);
							CacheResponse response = new CacheResponse(httpResponse);
							response.setExtra(request.getExtra());
							response.setTag(CacheResponse.UPDATE);
							diskCache.addResponseToCache(url, response);
							publishProgress(response);
						}
					} else {
						HttpResponse httpResponse = getWebData(url);
						CacheResponse response = new CacheResponse(httpResponse);
						response.setExtra(request.getExtra());
						response.setCached(false);
						diskCache.addResponseToCache(url, response);
						publishProgress(response);
					}
				} catch (DiskReadException diskException) {
					try {
						HttpResponse httpResponse = getWebData(url);
						CacheResponse response = new CacheResponse(httpResponse);
						response.setExtra(request.getExtra());
						response.setCached(false);
						diskCache.addResponseToCache(url, response);
						publishProgress(response);
					} catch (Exception e) {
						publishProgress(e, request.getExtra());
					}
				} catch (Exception e) {
					publishProgress(e, request.getExtra());
				}
			}
		}
		return true;
	}
	
	@Override
	protected void onProgressUpdate(Object... progress) {
		super.onProgressUpdate(progress);
		if (progress[0] instanceof Exception) {
			Exception exception = (Exception) progress[0];
			String extra = (String) progress[1];
			listener.onCacheError(exception, extra);
		} else {
			CacheResponse response = (CacheResponse) progress[0];
			listener.onCachedResponseReceived(response);
		}
	}

}
