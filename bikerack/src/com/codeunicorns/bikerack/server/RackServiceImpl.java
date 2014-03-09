package com.codeunicorns.bikerack.server;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.codeunicorns.bikerack.client.Rack;
import com.codeunicorns.bikerack.client.RackService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
// This class is to communicate with client and send Racks info to it
public class RackServiceImpl extends RemoteServiceServlet implements RackService {
	private static Rack[] racks = null;
	// TODO: have to initialized to something
	private static String[] params = null;

	public Rack[] getRacks() {
		return racks;
	}
	
	public String[] getTableView() {
		return params;
	}
	
	public void setRacks(Rack[] racks) {
		this.racks = racks;
	}
	
	public void setParams(String[] params) {
		this.params = params;
	}
}
