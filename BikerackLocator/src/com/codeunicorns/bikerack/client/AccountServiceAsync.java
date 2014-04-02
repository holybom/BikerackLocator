package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AccountServiceAsync {
	void login(String[] request, AsyncCallback<LoginInfo> callback);
	void register(String[] request, AsyncCallback<LoginInfo> callback);
	void saveFavoriteRacks(Long id, Rack[] favorites, AsyncCallback<Boolean> callback);
}
