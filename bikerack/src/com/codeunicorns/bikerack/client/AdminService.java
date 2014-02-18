package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	void setDataURL(String uri);
	void importData();
	void loadData();
	void setTableView(String[] params);
}
