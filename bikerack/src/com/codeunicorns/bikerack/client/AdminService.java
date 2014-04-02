package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	Boolean setDataURL(String uri);
	String getDataURL();
	Boolean importData();
	String[] getTitleLine();
	Boolean loadData(String[] params);
	Boolean setTableView(String[] params);
	Boolean setRacks(Rack[] racks);
	Boolean userNotify(String notification);
}
