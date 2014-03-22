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
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.codeunicorns.bikerack.client.Rack;
import com.codeunicorns.bikerack.client.RackService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
// This class is to communicate with client and send Racks info to it
public class RackServiceImpl extends RemoteServiceServlet implements RackService {
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static PersistenceManager pm;
	private Rack[] racks = null;
	// TODO: have to initialized to something
	private static String[] params = null;

	public Rack[] getRacks() {
		// Wipe data on load for testing
		//deleteAllRacks();
		pm = PMF.getPersistenceManager();
		List<Rack> results;
		try {
		Query q = pm.newQuery(Rack.class);
		//q.setOrdering("createDate descending");
		results = (List<Rack>) q.execute();
		}
		finally {
			pm.close();
		}
		racks = new Rack[results.size()];
		for (int i = 0; i < racks.length; i++) {
			//System.out.println("get: " + results.get(i).getStreetNum() + " " + results.get(i).getStreetName());
			racks[i] = results.get(i);
		}
		//System.out.println("Get: Number of Racks: " + racks.length);
		
		return racks;
	}
	
	public String[] getTableView() {
		return params;
	}
	
	public boolean setRacks(Rack[] racks) {
		//this.racks = racks;
		//System.out.println("getBeforeSet");
		for (int i = 0; i <= 3; i++) deleteAllRacks();
		pm = PMF.getPersistenceManager();
		//System.out.println("Set: Number of Racks: " + racks.length);
		try {
//		for (Rack rack : racks) {
//			System.out.println("set: " + rack.getStreetNum() + " " + rack.getStreetName());
//			pm.makePersistent(new Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(),
//					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng()));
			pm.makePersistentAll(racks);
		}
		finally {
		//	pm.refreshAll();
			pm.close();
		}
		//System.out.println("set All");
		//getRacks();
		return true; 
	}
	
	public void setParams(String[] params) {
		this.params = params;
	}
	
	public void deleteAllRacks() {
		pm = PMF.getPersistenceManager();
		try {
			Query q = pm.newQuery(Rack.class);
			//List<Rack> results = (List<Rack>) q.execute();
			//for (Rack rack : results) {
				//System.out.println("delete: " + rack.getStreetNum() + " " + rack.getStreetName());
				//pm.deletePersistent(rack);
			//}
			q.setFilter("lat > minLat");
			q.declareParameters("int minLat");
			q.deletePersistentAll(-9999);
//			System.out.println("Delete: Number of Racks: " + results.size());
//			q.deletePersistentAll(results);
			}
			finally {
				//pm.refreshAll();
				pm.close();
			}
		//System.out.println("deleted ALL");
		//getRacks();
	}
}
