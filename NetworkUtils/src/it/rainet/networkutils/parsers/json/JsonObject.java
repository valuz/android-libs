package it.rainet.networkutils.parsers.json;

import java.util.HashMap;
import java.util.Map;

public class JsonObject extends JsonAggregator {

	private Map<String, Object> values;
	
	public JsonObject(Map<String, Object> values) {
		this.values = values;
	}
	
	public JsonObject getObject(String key) {
		Object object = values.get(key);
		if (object instanceof JsonObject)
			return (JsonObject) object;
		return new NullJsonObject();
	}

	public JsonArray getArray(String key) {
		Object object = values.get(key);
		if (object instanceof JsonArray)
			return (JsonArray) object;
		return new JsonArray.NullJsonArray();
	}

	public String getString(String key) {
		Object object = values.get(key);
		if (object instanceof String)
			return (String) object;
		return "";
	}

	public Boolean getBoolean(String key) {
		Object object = values.get(key);
		if (object instanceof Boolean)
			return (Boolean) object;
		return false;
	}

	public Double getNumber(String key) {
		Object object = values.get(key);
		if (object instanceof Float)
			return (Double) object;
		return 0.0;
	}

	@Override
	public int size() {
		return values.keySet().size();
	}

	public Boolean hasKey(String key) {
		return values.containsKey(key);
	}

	@Override
	public String toString() {
		String jsonString = "{";
		for (String key : values.keySet()) {
			Object object = values.get(key);
			String value;
			if (object instanceof String) {
				value = "\"" + object + "\"";
			} else
				value = object.toString();
			jsonString += "\"" + key + "\":" + value + ",";
		}
		if (jsonString.endsWith(",")) {
			jsonString = jsonString.substring(0, jsonString.length() -1);
		}
		jsonString += "}";
		return jsonString;
	}

	public static class NullJsonObject extends JsonObject {

		public NullJsonObject() {
			super(new HashMap<String, Object>());
		}
		
		public JsonObject getObject(String key) {
			return this;
		}

		public JsonArray getArray(String key) {
			return new JsonArray.NullJsonArray();
		}

		public String getString(String key) {
			return "";
		}

		public Boolean getBoolean(String key) {
			return false;
		}

		public Double getNumber(String key) {
			return 0.0;
		}
		
		public Boolean hasKey(String key) {
			return false;
		}

	}
}
