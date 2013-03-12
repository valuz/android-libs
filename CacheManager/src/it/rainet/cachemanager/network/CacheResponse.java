package it.rainet.cachemanager.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class CacheResponse implements Serializable {

	/**
	 * versione 1.0
	 * 10L
	 */
	private static final long serialVersionUID = 11L;

	public static final int CACHED = 0;
	public static final int UPDATE = 1;
	
	private boolean cached = false;
	private Map<String, List<String>> headers;
	private byte[] bytes;
	private int statusCode;
	private int tag;
	private String extra;
	
	public CacheResponse(int statusCode, byte[] bytes, Map<String, List<String>> headers) {
		this.statusCode = statusCode;
		this.bytes = bytes;
		this.headers = headers;
		setTag(CACHED);
	}
	
	public CacheResponse(HttpResponse response) {
		statusCode = response.getStatusLine().getStatusCode();
		headers = getHeaders(response.getAllHeaders());
		bytes = getBytes(response);
		setTag(CACHED);
	}
	
	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String getHeader(String name) {
		if (headers.containsKey(name))
			return headers.get(name).get(0);
		return "";
	}

	public List<String> getHeaders(String name) {
		if (headers.containsKey(name))
			return headers.get(name);
		return new ArrayList<String>();
	}

	public Map<String, List<String>> getAllHeaders() {
		return headers;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return this.tag;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	private static Map<String, List<String>> getHeaders(Header[] headers) {
		Map<String, List<String>> headersMap = new HashMap<String, List<String>>();
		for (Header header : headers) {
			if (headersMap.containsKey(header.getName())) {
				List<String> temp = headersMap.get(header.getName());
				temp.add(header.getValue());
				headersMap.put(header.getName(), temp);
			} else {
				List<String> temp = new ArrayList<String>();
				temp.add(header.getValue());
				headersMap.put(header.getName(), temp);
			}
		}
		return headersMap;
	}

	private static byte[] getBytes(HttpResponse response) {
		byte[] bytes = new byte[0];
		try {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new IllegalArgumentException("HTTP entity may not be null");
			}
			InputStream content = entity.getContent();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
				content = new GZIPInputStream(content);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				byte[] tmp = new byte[1024];
				int l;
				while ((l = content.read(tmp)) != -1) {
				buffer.write(tmp, 0, l);
				}
				bytes = buffer.toByteArray();
			} finally {
				content.close();
				buffer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
}
