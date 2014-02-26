package com.codeunicorns.bikerack.server;

import com.codeunicorns.bikerack.client.RackService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RackServiceImpl extends RemoteServiceServlet implements RackService {
	public com.codeunicorns.bikerack.client.Rack[] getRacks() {
		return null;
	}
	public String[] getTableView() {
		return null;
	}
}
