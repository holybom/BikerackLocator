package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface RackServiceAsync {
	void getRacks(AsyncCallback<Rack[]> callback);
	void getTableView(AsyncCallback<String[]> callback);
}
