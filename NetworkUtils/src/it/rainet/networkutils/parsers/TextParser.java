package it.rainet.networkutils.parsers;


public class TextParser extends Parser<String> {

	public String parseResponse(byte[] response) {
		return readResponse(response).trim();
	}

}
