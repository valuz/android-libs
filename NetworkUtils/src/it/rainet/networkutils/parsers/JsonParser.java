package it.rainet.networkutils.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.rainet.networkutils.parsers.json.JsonAggregator;
import it.rainet.networkutils.parsers.json.JsonArray;
import it.rainet.networkutils.parsers.json.JsonObject;

public class JsonParser extends Parser<JsonAggregator> {

	@Override
	public JsonAggregator parseResponse(byte[] response) {
		String responseText = readResponse(response);
		try {
			JsonAggregator JsonAggregator = buildJson(responseText);
			return JsonAggregator;
		} catch (Exception e) {
			return null;
		}
	}

	private JsonAggregator buildJson(String responseText) throws Exception {
		if (responseText.startsWith("{")) {
			JSONObject object = new JSONObject(responseText);
			return getAggregator(object);
		} else if (responseText.startsWith("[")) {
			JSONArray array = new JSONArray(responseText);
			return getAggregator(array);
		}
		return null;
	}

	private JsonAggregator getAggregator(JSONArray array) throws JSONException {
		ArrayList<Object> values = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object object = array.get(i);
			if (object instanceof JSONArray) {
				values.add(getAggregator((JSONArray) object));
			} else if(object instanceof JSONObject) {
				values.add(getAggregator((JSONObject) object));
			} else if (object instanceof String) {
				values.add((String) object);
			} else if (object instanceof Boolean) {
				values.add((Boolean) object);
			} else {
				double optDouble = array.optDouble(i);
				values.add(optDouble);
			}
		}
		return new JsonArray(values);
	}

	private JsonAggregator getAggregator(JSONObject jsonObject) throws JSONException {
		Map<String, Object> values = new HashMap<String, Object>();
		JSONArray keySet = jsonObject.names();
		for (int i = 0; i < keySet.length(); i++) {
			String key = keySet.getString(i);
			Object object = jsonObject.get(key);
			if (object instanceof JSONArray) {
				values.put(key, getAggregator((JSONArray) object));
			} else if(object instanceof JSONObject) {
				values.put(key, getAggregator((JSONObject) object));
			} else if (object instanceof String) {
				values.put(key, (String) object);
			} else if (object instanceof Boolean) {
				values.put(key, (Boolean) object);
			} else {
				double optDouble = keySet.optDouble(i);
				values.put(key, optDouble);
			}
		}
		return new JsonObject(values);
	}
}
