package it.rainet.networkutils.response.listeners;

import it.rainet.networkutils.response.NetworkResponse;

public interface NetworkResponseListener {

	void onResponseReceived(NetworkResponse networkResponse);

	void onClientError(Exception exception, String extra);
}
