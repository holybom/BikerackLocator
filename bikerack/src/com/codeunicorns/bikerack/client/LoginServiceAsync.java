package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface LoginServiceAsync {
	void login(String[] request, AsyncCallback<LoginInfo> callback);
}
