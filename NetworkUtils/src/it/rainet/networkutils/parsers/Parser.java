package it.rainet.networkutils.parsers;

import java.io.IOException;

import android.net.ParseException;
import android.util.Log;

public abstract class Parser<T> {

	public abstract T parseResponse(byte[] response);

	protected static String readResponse(byte[] response) {
		try {
			return getResponseBody(response);
		} catch (IllegalStateException e) {
			Log.e("Response Parser", e.getMessage());
			return "Response Error";
		} catch (ParseException e) {
			Log.e("Response Parser", e.getMessage());
			return "Response Error";
		} catch (IOException e) {
			Log.e("Response Parser", e.getMessage());
			return "Response Error";
		}
	}

	private static String getResponseBody(final byte[] response) throws IOException, ParseException {
		if (response == null) {
			return "";
		}
		return new String(response, "UTF-8");
	}

}
