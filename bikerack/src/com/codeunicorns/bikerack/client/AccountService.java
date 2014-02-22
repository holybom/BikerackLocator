package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("account")
public interface AccountService extends RemoteService {
	LoginInfo login(String[] request);
	LoginInfo register(String[] request);
}
