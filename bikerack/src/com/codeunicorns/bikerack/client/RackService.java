package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("rack")
public interface RackService extends RemoteService {
	Rack[] getRacks();
	String[] getTableView();
}
