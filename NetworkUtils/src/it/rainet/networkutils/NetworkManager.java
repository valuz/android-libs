package it.rainet.networkutils;

import it.rainet.networkutils.request.NetworkGetRequest;
import it.rainet.networkutils.request.NetworkPostRequest;
import it.rainet.networkutils.response.listeners.NetworkResponseListener;
import it.rainet.networkutils.task.NetworkAsyncTask;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

	public class NoConnectionException extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		public NoConnectionException() {
			super("Device not connected");
		}
	}

	public static final String NO_CONNECTION_TAG = "no_connection";
	private Context context;
	private static NetworkManager instance;

	public static void initInstance(Context context) {
		instance = new NetworkManager(context);
	}

	public static NetworkManager getInstance() {
		return instance;
	}

	public NetworkManager(Context context) {
		this.context = context;
	}

	public NetworkAsyncTask getWebData(String url, NetworkResponseListener listener) {
		NetworkGetRequest get = new NetworkGetRequest(url);
		return getWebData(new BasicHttpContext(), get, listener, "");
	}
	
	public NetworkAsyncTask getWebData(HttpContext context, String url, NetworkResponseListener listener) {
		NetworkGetRequest get = new NetworkGetRequest(url);
		return getWebData(context, get, listener, "");
	}
	
	public NetworkAsyncTask getWebData(String url, NetworkResponseListener listener, String extra) {
		NetworkGetRequest get = new NetworkGetRequest(url);
		return getWebData(new BasicHttpContext(), get, listener, extra);
	}
	
	public NetworkAsyncTask getWebData(HttpContext context, String url, NetworkResponseListener listener, String extra) {
		NetworkGetRequest get = new NetworkGetRequest(url);
		return getWebData(context, get, listener, extra);
	}
	
	public NetworkAsyncTask getWebData(NetworkGetRequest get, NetworkResponseListener listener) {
		return getWebData(new BasicHttpContext(), get, listener, "");
	}

	public NetworkAsyncTask getWebData(HttpContext context, NetworkGetRequest get, NetworkResponseListener listener) {
		return getWebData(context, get, listener, "");
	}
	
	public NetworkAsyncTask getWebData(NetworkGetRequest get, NetworkResponseListener listener, String extra) {
		return getWebData(new BasicHttpContext(), get, listener, extra);
	}
	
	public NetworkAsyncTask getWebData(HttpContext context, NetworkGetRequest get, NetworkResponseListener listener, String extra) {
		NetworkAsyncTask networkAsyncTask = new NetworkAsyncTask(context, listener);
		networkAsyncTask.setExtra(extra);
		if (isInternetConnectionAlive()) {
			networkAsyncTask.execute(get.buildRequest());
		} else {
			listener.onClientError(new NoConnectionException(), NO_CONNECTION_TAG);
		}
		return networkAsyncTask;
	}
	
	public NetworkAsyncTask postWebData(NetworkPostRequest post, NetworkResponseListener listener) {
		return postWebData(new BasicHttpContext(), post, listener, "");
	}
	
	public NetworkAsyncTask postWebData(HttpContext context, NetworkPostRequest post, NetworkResponseListener listener) {
		return postWebData(context, post, listener, "");
	}
	
	public NetworkAsyncTask postWebData(NetworkPostRequest post, NetworkResponseListener listener, String extra) {
		return postWebData(new BasicHttpContext(), post, listener, extra);
	}
	
	public NetworkAsyncTask postWebData(HttpContext context, NetworkPostRequest post, NetworkResponseListener listener, String extra) {
		NetworkAsyncTask networkAsyncTask = new NetworkAsyncTask(context, listener);
		networkAsyncTask.setExtra(extra);
		if (isInternetConnectionAlive()) {
			networkAsyncTask.execute(post.buildRequest());
		} else {
			listener.onClientError(new NoConnectionException(), NO_CONNECTION_TAG);
		}
		return networkAsyncTask;
	}
	
	private boolean isInternetConnectionAlive() {
		ConnectivityManager connectivityService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityService.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}
}
