package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void setDataURL(String uri, AsyncCallback<Boolean> callback);
	void importData(AsyncCallback<Boolean> callback);
	void getTitleLine(AsyncCallback<String[]> callback);
	void loadData(String[] params, AsyncCallback<Boolean> callback);
	void setTableView(String[] params, AsyncCallback<Boolean> callback);
}
