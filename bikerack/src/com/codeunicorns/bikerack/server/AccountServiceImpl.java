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
		LoginInfo loginInfo = new LoginInfo(user.getEmailAddress(), user.getNickName(), user.getFacebookId(), type, serverRacksToClientRacks(rackIdsToRacks(user.getFavorites())), user.getId());
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
		Long[] favoritesInStore = new Long[favorites.length];
		User user;
		boolean noError = true;
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			user = pm.getObjectById(User.class, id);
			for (int i = 0; i < favorites.length; i++) {
				favoritesInStore[i] = favorites[i].getId();
			}
			user.setFavorites(favoritesInStore);
			//(new Date()).to;
		}
		finally {
			pm.close();
		}
		return new Boolean(noError);
	}

	Rack[] clientRacksToServerRacks(
			com.codeunicorns.bikerack.client.Rack[] favorites) {
		if (favorites == null || favorites.length < 1 || favorites[0] == null) return null;
		Long[] favoritesId = racksToRackIds(favorites);
		
//		for (int i = 0; i < favorites.length; i++) {
//			com.codeunicorns.bikerack.client.Rack rack = favorites[i];
//			racks[i] = new Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(), 
//					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng(), rack.getId());
//		}
		
		//System.out.println("Convert client to server racks: " + racks.length + " racks.");
		return racks;
	}
	
	
	com.codeunicorns.bikerack.client.Rack[] serverRacksToClientRacks(
			Rack[] favorites) {
		if (favorites == null) return null;
		com.codeunicorns.bikerack.client.Rack[] racks = new com.codeunicorns.bikerack.client.Rack[favorites.length];
		for (int i = 0; i < favorites.length; i++) {
			Rack rack = favorites[i];
			racks[i] = new com.codeunicorns.bikerack.client.Rack(rack.getStreetNum(), rack.getStreetName(), rack.getStreetSide(), 
					rack.getSkytrain(), rack.getbIA(), rack.getNumRacks(), rack.getLat(), rack.getLng(), rack.getId());
		}
		//System.out.println("Convert server to client racks: " + racks.length + " racks.");
		return racks;
	}
	
	private Rack[] rackIdsToRacks(Long[] ids) {
		if (ids == null || ids.length < 1 || ids[0] == null) return null;
		//System.out.println("User id decrypted to username: " + user.getUsername());
		Rack[] racks;
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
//			for (int i = 0; i < ids.length; i++) {
//				Query q = pm.newQuery(Rack.class);
//				q.setFilter("id == idParam");
//				q.declareParameters("String idParam");
//				List<Rack> racks = (List<Rack>) q.execute(favorites[i].getId());
//				if (racks.size() > 1) {System.out.println("Error: Duplicate id's: " + racks.get(0).getId()); noError = false;}
//				else if (racks.size() < 1) {System.out.println("Error: Non-existent Rack with id: " + favorites[i].getId()); noError = false;}
//				else favoritesInStore[i] = racks.get(0);	
//			}
			racks = (Rack[]) pm.getObjectsById((Object[]) ids);
			if (racks.length != ids.length) System.out.println("Error: Cannot get all the racks, Need: " + ids.length + ", Got: " + racks.length);
		}	
		finally {
			pm.close();
		}
//		if (user == null) {System.out.println("Error: Can't find user: " + id); return new Boolean(false);}
//		if (favoritesInStore.length < 1 || favoritesInStore[0] == null) return new Boolean(false);
		
		return racks;
	}
	

	private Long[] racksToRackIds(com.codeunicorns.bikerack.client.Rack[] favorites) {
		// TODO Auto-generated method stub
		return null;
	}
}
