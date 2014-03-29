package com.codeunicorns.bikerack.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.codeunicorns.bikerack.client.LoginInfo;
import com.codeunicorns.bikerack.client.AccountService;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.xml.internal.ws.developer.UsesJAXBContext;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

public class AccountServiceImpl extends RemoteServiceServlet implements AccountService {
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private String adminCode = "abcd0";

	/**
	 * @param request string contains [username, password] or [facebookId]
	 */
	public LoginInfo login(String[] request) {
		// Enable this to wipe database for testing purpose
		//deleteAllUsers();
		//System.out.println(request[0]);
		LoginInfo loginInfo = new LoginInfo("","","",1,null,(long) 0);
		List<User> users = retrieveAllUsers();
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			if (request.length == 1) {
				//System.out.println("client requests fb login: " + user.getFacebookId());
				if (request[0].compareTo(user.getFacebookId()) == 0) {
					System.out.println("Found user has " + user.getFavorites().length + " favorite racks");
					loginInfo = new LoginInfo("", user.getNickName(), user.getFacebookId(), 2, serverRacksToClientRacks(rackIdsToRacks(user.getFavorites())), user.getId());
					break;
				}
				//assert(request[0] != user.getFacebookId());
				continue;
			}
			if (user.getUsername().compareTo(request[0]) == 0 && user.getPassword().compareTo(request[1]) == 0) {
				loginInfo = new LoginInfo(user.getEmailAddress(), user.getNickName(), "", user.isAdmin() ? 4 : 3, serverRacksToClientRacks(rackIdsToRacks(user.getFavorites())), user.getId());
				break;
			}
		}
		return loginInfo;
	}

	private List<User> retrieveAllUsers() {
		List<User> users;
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
		Query q = pm.newQuery(User.class);
		q.setOrdering("createDate descending");
		users = (List<User>) q.execute();
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			//System.out.println("Database:" + i + ". " + user.getUsername() + " " + user.getPassword() + user.getId());
		}
		}finally {
			pm.close();
		}
		return users;
	}

	/**
	 * Check duplicate username
	 * @param userName
	 * @return
	 */
	private boolean checkDuplicate(String content, String type) {
		if (content == null) return true;
		PersistenceManager pm = PMF.getPersistenceManager();
		boolean isDuplicate = false;
		List<User> users = retrieveAllUsers();
		if (type.compareTo("username") == 0) {
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getUsername().compareTo(content) == 0) {
					isDuplicate = true;
					break;
				}
			}
		}
		if (type.compareTo("facebookid") == 0) {
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getFacebookId().compareTo(content) == 0) {
					isDuplicate = true;
					break;
				}
			}
		}
		return isDuplicate;
	}

	private void deleteAllUsers() {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Query q = pm.newQuery(User.class);;
			List<User> users = (List<User>) q.execute();
			pm.deletePersistentAll(users);
		} finally {
			pm.close();
		}
	}

/**
 * facebookId = null for normal users
 * @param request string is [email, nickname, username, password, admin code] or [facebookId]; 
 */
	public LoginInfo register(String[] request) {
		User user;
		int type;
		if (request.length == 2) {
			System.out.println("client request fb register");
			String facebookId = request[0];
			if (checkDuplicate(facebookId, "facebookid")) return null;
			user = new User("",request[1],"","",false, facebookId, true, null);
			type = 2;
		}
		else if (request.length == 5) {
			String emailAddress = request[0];
			String nickName = request[1];
			String userName = request[2];
			String password = request[3];
			String inputAdminCode = request[4];
			if (checkDuplicate(userName, "username")) return null;
			type = 3;
			if (inputAdminCode.compareTo(adminCode) == 0) type = 4;
			user = new User(emailAddress, nickName, userName, password, type == 4? true : false, "", false, null);
		}  
		else return null;
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
		pm.makePersistent(user);
		}
		finally {
			pm.refreshAll();
			pm.close();
		}
		LoginInfo loginInfo = new LoginInfo(user.getEmailAddress(), user.getNickName(), user.getFacebookId(), type, null, user.getId());
		return loginInfo;
	}

	public Boolean saveFavoriteRacks(Long id, com.codeunicorns.bikerack.client.Rack[] favorites) {
//		List<User> users = retrieveAllUsers();
//		User user = null;
//		for (int i = 0; i < users.size(); i++) {
//			User result = users.get(i);
//			if (result.getId() == id) {
//				user = result;
//				break;
//			}
//		}
//		if (user == null || i >= users.size()) return new Boolean(false);
//		user.setFavorites(favorites);
		//System.out.println("id: " + id + ", Length2Save: " + favorites.length);
		User user;
		boolean noError = true;
		PersistenceManager pm = PMF.getPersistenceManager();
		System.out.println("Requested save " + favorites.length + " racks");
		try {
			user = pm.getObjectById(User.class, id);
			Long[] ids = racksToRackIds(favorites);
			user.setFavorites(ids);
			//(new Date()).to;
			if (ids.length != favorites.length) noError = false;
		}
		finally {
			pm.close();
		}
		return new Boolean(noError);
	}
	
	com.codeunicorns.bikerack.client.Rack[] serverRacksToClientRacks(
			Rack[] favorites) {
		if (favorites == null || (favorites.length >= 1 && favorites[0] == null)) return null;
		com.codeunicorns.bikerack.client.Rack[] racks = new com.codeunicorns.bikerack.client.Rack[favorites.length];
		for (int i = 0; i < favorites.length; i++) {
			Rack rack = favorites[i];
			racks[i] = new com.codeunicorns.bikerack.client.Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(), 
					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng(), rack.getId());
		}
		if (racks == null) {System.out.println("Error: racks = null, how?"); return null;}
		System.out.println("Converted " + racks.length + " server to client racks.");
		return racks;
	}
	
	Rack[] clientRacksToServerRacks(
			com.codeunicorns.bikerack.client.Rack[] favorites) {
		if (favorites == null || (favorites.length >= 1 && favorites[0] == null)) return null;
		if (favorites.length < 1) return new Rack[0];
		Rack[] racks;
		if (favorites[0].getId() != null) racks = rackIdsToRacks(racksToRackIds(favorites));
//		for (int i = 0; i < favorites.length; i++) {
//			com.codeunicorns.bikerack.client.Rack rack = favorites[i];
//			racks[i] = new Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(), 
//					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng(), rack.getId());
//		}
		else {
			System.out.println("Bypassed persistence when converting client to server Racks");
			racks = new Rack[favorites.length];
			for (int i = 0; i < favorites.length; i++) {
			com.codeunicorns.bikerack.client.Rack rack = favorites[i];
			racks[i] = new Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(),
					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng());
			}
		}
		if (racks == null) {System.out.println("Error: racks = null, how two?"); return null;}
		System.out.println("Converted " + racks.length + " client to server racks.");
		return racks;
	}
	
	private Rack[] rackIdsToRacks(Long[] ids) {
		if (ids == null || (ids.length >= 1 && ids[0] == null)) return null;
		//System.out.println("User id decrypted to username: " + user.getUsername());
		if (ids.length < 1) return new Rack[0];
		Rack[] racks = new Rack[ids.length];
		PersistenceManager pm = PMF.getPersistenceManager();
		for (Long id : ids) {
			System.out.println("Request rack of id: " + id + " from datastore");
		}
		try {
			int length = ids.length;
			int i = 0;
			while (i < length) {
				System.out.println("pre i = " + i + " and length = " + length);
				Rack rack = pm.getObjectById(Rack.class, ids[i]);
				//if (racks.size() > 1) {System.out.println("Error: Duplicate id's: " + racks.get(0).getId()); noError = false;}
				if (rack == null) {
					System.out.println("Error: Non-existent Rack with id: " + ids[i]);
					length--;
					continue; 
				}
				else racks[i] = rack;
				i++;
			}
			System.out.println("post i = " + i + " and length = " + length);
			//racks = (Rack[]) pm.getObjectsById((Object[]) ids);
			if (length != ids.length) {
				System.out.println("Error: Cannot get all the racks, Need: " + ids.length + ", Got: " + length);
				Rack[] reRacks = new Rack[i+1];
				while (i > 0) {
					reRacks[i] = racks[i];
					i--;
				}
				racks = reRacks;
			}
		}	
		finally {
			pm.close();
		}
//		if (user == null) {System.out.println("Error: Can't find user: " + id); return new Boolean(false);}
//		if (favoritesInStore.length < 1 || favoritesInStore[0] == null) return new Boolean(false);
		System.out.println("Converted " + racks.length + " ids into Racks");
		return racks;
	}
	

	private Long[] racksToRackIds(com.codeunicorns.bikerack.client.Rack[] favorites) {
		if (favorites == null || (favorites.length >= 1 && favorites[0] == null)) return null;
		Long[] ids = new Long[favorites.length];
		for (int i = 0; i < favorites.length; i++) {
			ids[i] = favorites[i].getId();
		}
		System.out.println("Converted " + ids.length + " racks to Ids");
		return ids;
	}
}
