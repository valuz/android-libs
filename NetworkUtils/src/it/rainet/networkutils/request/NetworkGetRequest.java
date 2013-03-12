package it.rainet.networkutils.request;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class NetworkGetRequest extends NetworkRequest {

	private Map<String, String> parameters;
	private HttpGet request;

	public NetworkGetRequest(String url) {
		request = new HttpGet();
		request.removeHeaders("If-None-Match");
		request.removeHeaders("Last-Modified");
		request.addHeader("Accept-Encoding", "gzip");
		try {
			URI uri = encodeUrl(url);
			request.setURI(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters = new HashMap<String, String>();
	}
	
	@Override
	public HttpUriRequest buildRequest() {
		if (!parameters.isEmpty()) {
			HttpParams params = new BasicHttpParams();
			for (String key : parameters.keySet()) {
				params.setParameter(key, parameters.get(key));
			}
			request.setParams(params);
		}
		return request;
	}
	
	@Override
	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}

	@Override
	protected HttpUriRequest getRequest() {
		return request;
	}

}
