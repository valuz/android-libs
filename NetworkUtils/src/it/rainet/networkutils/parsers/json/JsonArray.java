package it.rainet.networkutils.parsers.json;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonAggregator {

	private List<Object> values;
	
	public JsonArray(List<Object> values) {
		this.values = values;
	}

	public JsonObject getObject(Integer index) {
		Object object = values.get(index);
		if (object instanceof JsonObject)
			return (JsonObject) object;
		return new JsonObject.NullJsonObject();
	}

	public JsonArray getArray(Integer index) {
		Object object = values.get(index);
		if (object instanceof JsonArray)
			return (JsonArray) object;
		return new NullJsonArray();
	}

	public String getString(Integer index) {
		Object object = values.get(index);
		if (object instanceof String)
			return (String) object;
		return "";
	}

	public Boolean getBoolean(Integer index) {
		Object object = values.get(index);
		if (object instanceof Boolean)
			return (Boolean) object;
		return false;
	}

	public Double getNumber(Integer index) {
		Object object = values.get(index);
		if (object instanceof Float)
			return (Double) object;
		return 0.0;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public String toString() {
		String jsonString = "[";
		for (Object object : values) {
			String value;
			if (object instanceof String)
				value = "\"" + object + "\"";
			else
				value = object.toString();
			jsonString += value + ",";
		}
		if (jsonString.endsWith(",")) {
			jsonString = jsonString.substring(0, jsonString.length() - 1);
		}
		jsonString += "]";
		return jsonString;
	}
	
	public static class NullJsonArray extends JsonArray {

		public NullJsonArray() {
			super(new ArrayList<Object>());
		}
		public JsonObject getObject(Integer index) {
			return new JsonObject.NullJsonObject();
		}

		public JsonArray getArray(Integer index) {
			return this;
		}

		public String getString(Integer index) {
			return "";
		}

		public Boolean getBoolean(Integer index) {
			return false;
		}

		public Double getNumber(Integer index) {
			return 0.0;
		}
	}

}
