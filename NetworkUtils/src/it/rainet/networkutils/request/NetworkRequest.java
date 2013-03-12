package it.rainet.networkutils.request;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import android.net.Uri;

public abstract class NetworkRequest {

	
	public abstract HttpUriRequest buildRequest();

	public abstract void addParameter(String name, String value);
	
	protected abstract HttpUriRequest getRequest();

	public void addHeader(String name, String value) {
		Header header = new BasicHeader(name, value);
		getRequest().addHeader(header);
	}

	public void removeHeader(String name) {
		getRequest().removeHeaders(name);
	}
	
	protected URI encodeUrl(String url) throws URISyntaxException {
		Uri uriParser = Uri.parse(url);
		return new URI(uriParser.getScheme(),
				uriParser.getEncodedUserInfo(),
				uriParser.getHost(),
				uriParser.getPort(),
				uriParser.getEncodedPath(),
				uriParser.getEncodedQuery(),
				uriParser.getEncodedFragment());
	}
	
}
