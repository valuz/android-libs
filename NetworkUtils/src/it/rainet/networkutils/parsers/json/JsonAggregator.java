package it.rainet.networkutils.parsers.json;

public abstract class JsonAggregator {
	
	public abstract int size();
	
	public abstract String toString();

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean isJsonArray() {
		return (this instanceof JsonArray);
	}
	
	public JsonArray getJsonArray() {
		return (JsonArray) this;
	}

	public boolean isJsonObject() {
		return (this instanceof JsonObject);
	}
	
	public JsonObject getJsonObject() {
		return (JsonObject) this;
	}
}
