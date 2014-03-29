package com.codeunicorns.bikerack.server;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.codeunicorns.bikerack.client.RackService;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
// This class is to communicate with client and send Racks info to it
public class RackServiceImpl extends RemoteServiceServlet implements RackService {
	private static final long serialVersionUID = 732266412467513628L;
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static PersistenceManager pm;
	private static Rack[] racks = null;
	// TODO: have to initialized to something
	private static String[] params = null;
	private static boolean bypassPersistence = false;

	@SuppressWarnings("unchecked")
	public com.codeunicorns.bikerack.client.Rack[] getRacks() {
		AccountServiceImpl asi = new AccountServiceImpl();
		if (bypassPersistence) {
			bypassPersistence = false;
			System.out.println("Bypassed persistence when retrieving racks for sending to client");
			return asi.serverRacksToClientRacks(racks);
		}
		// Wipe data on load for testing
		//deleteAllRacks();
		pm = PMF.getPersistenceManager();
		List<Rack> results;
		try {
		Query q = pm.newQuery(Rack.class);
		q.setOrdering("streetName asc, streetNum asc");
		results = (List<Rack>) q.execute();
		}
		finally {
			pm.close();
		}
		Rack[] racks = new Rack[results.size()];
		for (int i = 0; i < racks.length; i++) {
			System.out.println("get: " + results.get(i).getStreetNum() + " " + results.get(i).getStreetName() + " " + results.get(i).getId());
			racks[i] = results.get(i);
		}
		//System.out.println("Get: Number of Racks: " + racks.length);
		
		return asi.serverRacksToClientRacks(racks);
	}
	
	public String[] getTableView() {
		return params;
	}
	
	public boolean setRacks(Rack[] racks) {
		if (racks == null) return false;
		System.out.println("Set: Number of Racks: " + racks.length);
		RackServiceImpl.racks = racks;
		if (bypassPersistence) return true;
		//System.out.println("getBeforeSet");
		//for (int i = 0; i <= 3; i++)
		int i = getRacks().length;
		int f;
		//System.out.println("Set: Number of Racks: " + racks.length);
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Query q = pm.newQuery(Rack.class);
			List<Rack> results = (List<Rack>) q.execute();
			if (results != null && results.size() != 0) pm.deletePersistentAll(results);
			while ((f = getRacks().length) < i && f > 0) i = f;
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
		//System.out.print("After set: ");
		//getRacks();
		return true; 
	}
	
	public void setParams(String[] params) {
		RackServiceImpl.params = params;
	}
	
//	@SuppressWarnings("unchecked")
//	public void deleteAllRacks(PersistenceManager pm) {
//			Query q = pm.newQuery(Rack.class);
//			List<Rack> results = (List<Rack>) q.execute();
//			//for (Rack rack : results) {
//				//System.out.println("delete: " + rack.getStreetNum() + " " + rack.getStreetName());
//				//pm.deletePersistent(rack);
//			//}
////			q.setFilter("lat > minLat");
////			q.declareParameters("int minLat");
////			q.deletePersistentAll(-9999);
////			System.out.println("Delete: Number of Racks: " + results.size());
//			if (results != null && results.size() != 0) pm.deletePersistentAll(results);
//		System.out.print("After delete: ");
//		getRacks();
//	}
	
	void setBypassPersistence(boolean bypassPersistence) {
		RackServiceImpl.bypassPersistence = bypassPersistence;
	}
}
