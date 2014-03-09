package com.codeunicorns.bikerack.client;

import java.io.IOException;
import java.net.MalformedURLException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	Boolean setDataURL(String uri);
	Boolean importData();
	String[] getTitleLine();
	Boolean loadData(String[] params);
	Boolean setTableView(String[] params);
}
