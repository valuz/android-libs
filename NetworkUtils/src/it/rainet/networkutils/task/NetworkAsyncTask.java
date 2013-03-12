package it.rainet.networkutils.task;

import it.rainet.networkutils.response.NetworkResponse;
import it.rainet.networkutils.response.listeners.NetworkResponseListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;

public class NetworkAsyncTask extends AsyncTask<HttpUriRequest, Object, Object> {

	private String extra;
	private NetworkResponseListener listener;
	private HttpContext context;

	public NetworkAsyncTask(NetworkResponseListener listener) {
		this(new BasicHttpContext(), listener);
	}
	
	public NetworkAsyncTask(HttpContext context, NetworkResponseListener listener) {
		this.listener = listener;
		this.context = context;
	}
	
	@Override
	protected Object doInBackground(HttpUriRequest... params) {
		if (params.length <= 0) {
			return new NetworkRequestNotFoundException();
		}
		try {
			HttpUriRequest request = params[0];
			HttpClient client = new DefaultHttpClient();
			HttpResponse fullResponse = client.execute(request, context);
			NetworkResponse webResponse = buildNetworkResponse(fullResponse);
			return webResponse;
		} catch (Throwable e) {
			Log.e("NetworkManagerTask", e.getMessage());
			return e;
		}
	}
	
	@Override
	protected void onPostExecute(Object result) {
		if (result instanceof NetworkResponse) {
			NetworkResponse response = (NetworkResponse) result;
			listener.onResponseReceived(response);
		} else if (result instanceof Exception) {
			Exception exception = (Exception) result;
			listener.onClientError(exception, extra);
		}
	}

	private NetworkResponse buildNetworkResponse(HttpResponse fullResponse) {
		NetworkResponse response = new NetworkResponse(fullResponse);
		response.setExtra(extra);
		return response;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public class NetworkRequestNotFoundException extends Exception {
		
		private static final long serialVersionUID = 1001L;

		public NetworkRequestNotFoundException() {
			super("No request sended for this task");
		}
	}

}
