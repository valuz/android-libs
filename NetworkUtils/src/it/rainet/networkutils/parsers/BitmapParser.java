package it.rainet.networkutils.parsers;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapParser extends Parser<Bitmap> {

	@Override
	public Bitmap parseResponse(byte[] response) {
		try {
			Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
			return bitmap;
		} catch (IllegalStateException e) {
			return null;
		}
	}

}
