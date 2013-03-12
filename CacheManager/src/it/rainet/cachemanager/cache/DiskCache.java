package it.rainet.cachemanager.cache;

import it.rainet.cachemanager.network.CacheResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Log;

public class DiskCache {

	public class DiskWriteException extends Exception {
		
		private static final long serialVersionUID = 1L;

		public DiskWriteException(String message, Throwable throwable) {
			super(message, throwable);
		}
		
		public DiskWriteException(String message) {
			super(message);
		}

	}

	public class DiskReadException extends Exception {
		
		private static final long serialVersionUID = 2L;

		public DiskReadException(String message, Throwable throwable) {
			super(message, throwable);
		}
		
		public DiskReadException(String message) {
			super(message);
		}
	}

	private File cacheDir;

	public DiskCache(Context context) {
		cacheDir = context.getCacheDir();
	}

	public void addResponseToCache(String url, CacheResponse response) throws DiskWriteException {
		try {
			File file = new File(cacheDir, sStringToHMACMD5(url, "cache"));
			OutputStream fos = new FileOutputStream(file);
			ObjectOutputStream outputStream = new ObjectOutputStream(fos);
			outputStream.writeObject(response);
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			throw new DiskWriteException("Error writing response", e);
		}
	}
	
	public CacheResponse getCachedData(String url) throws DiskReadException {
		try {
			String md5Url = sStringToHMACMD5(url, "cache");
			File file = new File(cacheDir, md5Url);
			if (isCached(file))
				return readFile(file);
			throw new DiskReadException("No cache data found");
		} catch (Exception e) {
			throw new DiskReadException("Error reading response", e);
		}
	}
	
	public void clearOldFiles(Date expireDate) {
		for (String fileName : cacheDir.list()) {
			File temp = new File(cacheDir, fileName);
			if (temp.lastModified() < expireDate.getTime())
				temp.delete();
		}
	}

	public boolean isCached(String url) throws DiskReadException {
		try {
			String md5Url = sStringToHMACMD5(url, "cache");
			File file = new File(cacheDir, md5Url);
			return isCached(file);
		} catch (Exception e) {
			throw new DiskReadException("Error checking cache files", e);
		}
	}

	private boolean isCached(File file) {
		return file.exists() && file.isFile();
	}

	private CacheResponse readFile(File file) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream inputStream = new ObjectInputStream(fis);
			CacheResponse response = (CacheResponse) inputStream.readObject();
			inputStream.close();
			return response;
		} catch (Exception e) {
			if (e.getMessage() != null)
        		Log.e("DiskCache", e.getMessage());
			throw e;
		}
	}

	private String sStringToHMACMD5(String s, String keyString) throws Exception {
        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        byte[] bytes = mac.doFinal(s.getBytes("ASCII"));
        StringBuffer hash = new StringBuffer();

        for (int i=0; i<bytes.length; i++) {
            String hex = Integer.toHexString(0xFF &  bytes[i]);
            if (hex.length() == 1) {
                hash.append('0');
            }
            hash.append(hex);
        }
        return hash.toString() ;
    }

	public static DiskCache fromContext(Context context) {
		return new DiskCache(context);
	}
}
