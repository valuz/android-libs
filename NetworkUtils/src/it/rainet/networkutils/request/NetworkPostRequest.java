package it.rainet.networkutils.request;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Base64;
import android.util.Log;
import android.util.Pair;

public class NetworkPostRequest extends NetworkRequest {

	public final static int TYPE_FILE = 0;
	public final static int TYPE_STRING = 1;
	
	private HttpPost request;
	private Map<String, String> parameters;
	private Map<Integer, Pair<String, Object>> multipartParameters;
	
	public NetworkPostRequest(String url) {
		request = new HttpPost();
		request.removeHeaders("If-None-Match");
		request.removeHeaders("Last-Modified");
		request.addHeader("Accept-Encoding", "gzip");
		parameters = new HashMap<String, String>();
		multipartParameters = new Hashtable<Integer, Pair<String,Object>>();
		try {
			request.setURI(encodeUrl(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public HttpUriRequest buildRequest() {
		HttpEntity entity;
		if (multipartParameters.size() > 0)
			entity = buildMultipartEntity();
		else
			entity = buildDefaultEntity();
		request.setEntity(entity);
		return request;
	}

	@Override
	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}
	
	public void addMultipartParameter(String name, Object object, int type) {
		multipartParameters.put(type, new Pair<String, Object>(name, object));
	}
	
	public void addAuthenticationHeader(String login, String password) {
		addHeader("Authorization", getB64Auth(login, password));
	}

	@Override
	protected HttpUriRequest getRequest() {
		return request;
	}

	private HttpEntity buildMultipartEntity() {
		MultipartEntity entity = new MultipartEntity();
		for (Integer objectType : multipartParameters.keySet()) {
			Pair<String, Object> object = multipartParameters.get(objectType);
			try {
				switch (objectType) {
				case TYPE_FILE:
					entity.addPart(object.first, new FileBody((File) object.second));
					break;
				case TYPE_STRING:
					entity.addPart(object.first, new StringBody((String) object.second, Charset.forName("UTF-8")));
					break;
				default:
					break;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	private HttpEntity buildDefaultEntity() {
		HttpEntity entity = new BasicHttpEntity();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : parameters.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key)));
			try {
				entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				Log.e("AsyncPostRequest", e.getMessage());
			}
		}
		return entity;
	}

	private String getB64Auth (String login, String pass) {
		String source=login+":"+pass;
		String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
		return ret;
	}
}
