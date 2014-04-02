package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void setDataURL(String uri, AsyncCallback<Boolean> callback);
	void getDataURL(AsyncCallback<String> callback);
	void importData(AsyncCallback<Boolean> callback);
	void getTitleLine(AsyncCallback<String[]> callback);
	void loadData(String[] params, AsyncCallback<Boolean> callback);
	void setTableView(String[] params, AsyncCallback<Boolean> callback);
	void setRacks(Rack[] racks, AsyncCallback<Boolean> asyncCallback);
	void userNotify(String notification, AsyncCallback<Boolean> asyncCallback);
}
