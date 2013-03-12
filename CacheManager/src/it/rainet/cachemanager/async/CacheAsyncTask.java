package it.rainet.cachemanager.async;

import it.rainet.cachemanager.CacheRequest;
import it.rainet.cachemanager.cache.DiskCache;
import it.rainet.cachemanager.cache.DiskCache.DiskReadException;
import it.rainet.cachemanager.network.CacheResponse;
import it.rainet.cachemanager.network.listener.CacheResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CacheAsyncTask extends AsyncTask<CacheRequest, Object, Boolean> {

	public class NoConnectionException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		public NoConnectionException() {
			super("Device not connected");
		}
	}
	
	protected CacheResponseListener listener;
	protected DiskCache diskCache;
	protected String extra = "";
	private CacheResponse webResponse;
	protected Exception exception;
	private boolean followsReedirects;
	private boolean connectionAlive = true;

	public CacheAsyncTask(DiskCache diskCache, CacheResponseListener listener) {
		this.diskCache = diskCache;
		this.listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(CacheRequest... params) {
		if (params.length <= 0) {
			return false;
		}
		String url = params[0].getUrl();
		extra = params[0].getExtra();
		if (url == null || url.length() == 0) {
			exception = new RuntimeException("Url cannot be empty");
			return false;
		}
		try {
			CacheResponse cachedData = null;
			if (diskCache.isCached(url)) {
				cachedData = diskCache.getCachedData(url);
			}
			if (cachedData != null) {
				cachedData.setCached(true);
				if (extra != null)
					cachedData.setExtra(extra);
				webResponse = cachedData;
				HttpResponse headResponse = headWebData(url);
				Header contentLength = headResponse.getFirstHeader("Content-Length");
				String header = cachedData.getHeader("Content-Length");
				if (contentLength == null || !contentLength.getValue().equals(header)) {
					HttpResponse fullResponse = getWebData(url);
					webResponse = buildCacheResponse(fullResponse);
					webResponse.setTag(CacheResponse.UPDATE);
					diskCache.addResponseToCache(url, webResponse);
				}
				publishProgress();
				return true;
			} else {
				HttpResponse fullResponse = getWebData(url);
				webResponse = buildCacheResponse(fullResponse);
				diskCache.addResponseToCache(url, webResponse);
				publishProgress();
				return true;
			}
		} catch (DiskReadException diskException) {
			try {
				HttpResponse fullResponse = getWebData(url);
				webResponse = buildCacheResponse(fullResponse);
				diskCache.addResponseToCache(url, webResponse);
				publishProgress();
				return true;
			} catch (Exception e) {
				String errorMessage = "An error occurred searching for url: " + url;
				Log.e("CacheManagerTask", e.getMessage() != null?errorMessage + " - Exception: " + e.getMessage():errorMessage);
				exception = e;
				return false;
			}
		} catch (Exception e) {
			String errorMessage = "An error occurred searching for url: " + url;
			Log.e("CacheManagerTask", e.getMessage() != null?errorMessage + " - Exception: " + e.getMessage():errorMessage);
			exception = e;
			return false;
		}
	}
	
	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		if (webResponse != null) {
			listener.onCachedResponseReceived(webResponse);
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (!result) {
			listener.onCacheError(exception, extra);
		}
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setFollowsRedirects(boolean followsReedirects) {
		this.followsReedirects = followsReedirects;
	}

	public void setConnectionActive(boolean connectionAlive) {
		this.connectionAlive = connectionAlive;
	}

	private CacheResponse buildCacheResponse(HttpResponse httpResponse) {
		CacheResponse cacheResponse = new CacheResponse(httpResponse);
		if (extra != null)
			cacheResponse.setExtra(extra);
		return cacheResponse;
	}

	protected HttpResponse headWebData(String url) throws Exception {
		if (!connectionAlive) {
			throw new NoConnectionException();
		}
		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest head = new HttpHead(encodeUrl(url));
		head.addHeader("Accept-Encoding", "gzip");
		head.removeHeaders("If-None-Match");
		head.removeHeaders("If-Modified-Since");
		HttpResponse response = client.execute(head);
		return response;
	}

	protected HttpResponse getWebData(String url) throws Exception {
		if (!connectionAlive) {
			throw new NoConnectionException();
		}
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpClientParams.setRedirecting(params, followsReedirects);
		HttpGet get = new HttpGet(encodeUrl(url));
		get.addHeader("Accept-Encoding", "gzip");
		get.removeHeaders("If-None-Match");
		get.removeHeaders("If-Modified-Since");
		HttpResponse response = client.execute(get);
		return response;
	}

	private URI encodeUrl(String url) throws URISyntaxException {
		Uri uriAndroid = Uri.parse(url);
		URI uri = new URI(uriAndroid.getScheme(),
				uriAndroid.getEncodedUserInfo(),
				uriAndroid.getHost(),
				uriAndroid.getPort(),
				uriAndroid.getEncodedPath(),
				uriAndroid.getEncodedQuery(),
				uriAndroid.getEncodedFragment());
		return uri;
	}
}
