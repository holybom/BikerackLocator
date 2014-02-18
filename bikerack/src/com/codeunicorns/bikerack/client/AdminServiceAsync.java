package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void setDataURL(String uri, AsyncCallback<Void> callback);
	void importData(AsyncCallback<Void> callback);
	void loadData(AsyncCallback<Void> callback);
	void setTableView(String[] params, AsyncCallback<Void> callback);
}
