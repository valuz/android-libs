package it.rainet.cachemanager;

import it.rainet.cachemanager.async.CacheAsyncTask;
import it.rainet.cachemanager.async.MultipleCacheAsyncTask;
import it.rainet.cachemanager.cache.DiskCache;
import it.rainet.cachemanager.network.listener.CacheResponseListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CacheManager {
	
	private static CacheManager runningInstance;
	private DiskCache diskCache;
	private boolean followsReedirects = true;
	private Context context;

	public CacheManager(Context context) {
		this.context = context;
		diskCache = DiskCache.fromContext(context);
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 3);
		diskCache.clearOldFiles(calendar.getTime());
	}

	public CacheAsyncTask getCachedData(String url, CacheResponseListener listener) {
		return getCachedData(new CacheRequest(url, ""), listener);
	}
	
	public CacheAsyncTask getCachedData(String url, CacheResponseListener listener, String extra) {
		return getCachedData(new CacheRequest(url, extra), listener);
	}
	
	public CacheAsyncTask getCachedData(CacheRequest request, CacheResponseListener listener) {
		CacheAsyncTask cacheAsyncTask = new CacheAsyncTask(diskCache, listener);
		cacheAsyncTask.setFollowsRedirects(followsReedirects);
		cacheAsyncTask.setConnectionActive(isDeviceConnected());
		cacheAsyncTask.execute(request);
		return cacheAsyncTask;
	}
	
	public CacheAsyncTask getMultipleData(String[] urls, CacheResponseListener listener) {
		List<CacheRequest> requests = new ArrayList<CacheRequest>();
		for (String url : urls) {
			requests.add(new CacheRequest(url, ""));
		}
		return getMultipleData(requests, listener);
	}
	
	public CacheAsyncTask getMultipleData(List<CacheRequest> requests, CacheResponseListener listener) {
		MultipleCacheAsyncTask cacheAsyncTask = new MultipleCacheAsyncTask(diskCache, listener);
		cacheAsyncTask.setFollowsRedirects(followsReedirects);
		cacheAsyncTask.setConnectionActive(isDeviceConnected());
		cacheAsyncTask.execute(requests.toArray(new CacheRequest[requests.size()]));
		return cacheAsyncTask;
	}

	private boolean isDeviceConnected() {
		ConnectivityManager connectivityService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityService.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public void followsRedirects(boolean followsReedirects) {
		this.followsReedirects = followsReedirects;
	}

	public static CacheManager getInstance() {
		return runningInstance;
	}
	
	public static void initInstance(Context context) {
		runningInstance = new CacheManager(context);
	}
}
