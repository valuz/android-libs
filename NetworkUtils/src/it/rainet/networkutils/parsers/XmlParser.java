package it.rainet.networkutils.parsers;

import it.rainet.networkutils.readers.XmlReader;


import android.util.Log;

public class XmlParser extends Parser<XmlReader> {

	@Override
	public XmlReader parseResponse(byte[] response) {
		try {
			return new XmlReader(readResponse(response));
		} catch (IllegalStateException e) {
			Log.e("XmlParser", e.getMessage());
			return null;
		}
	}

}
