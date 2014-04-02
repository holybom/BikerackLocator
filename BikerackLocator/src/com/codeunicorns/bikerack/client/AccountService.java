package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("account")
public interface AccountService extends RemoteService {
	LoginInfo login(String[] request);
	LoginInfo register(String[] request);
	Boolean saveFavoriteRacks(Long id, Rack[] favorites);
}
