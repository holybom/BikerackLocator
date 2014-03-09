package com.codeunicorns.bikerack.server;

import java.util.ArrayList;
import java.util.Collection;
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
	 * @param request string contains [username, password]
	 */
	public LoginInfo login(String[] request) {
		// Enable this to wipe database for testing purpose
		deletePersistentAll();
		LoginInfo loginInfo = null;
		List<User> users = retrieveAllUsers();
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			if (user.getUsername().compareTo(request[0]) == 0 && user.getPassword().compareTo(request[1]) == 0) {
				loginInfo = new LoginInfo(user.getEmailAddress(), user.getNickName(), user.isAdmin());
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
			System.out.println("Database:" + i + ". " + user.getUsername() + " " + user.getPassword() + user.getId());
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
	private boolean checkDuplicate(String userName) {
		PersistenceManager pm = PMF.getPersistenceManager();
		boolean isDuplicate = false;
		List<User> users = retrieveAllUsers();
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUsername().compareTo(userName) == 0) {
				isDuplicate = true;
				break;
			}
		}
		return isDuplicate;
	}

	private void deletePersistentAll() {
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
 * @param request string is [email, nickname, username, password, admin code];
 */
	public LoginInfo register(String[] request) {
		assert(request.length == 5);
		String emailAddress = request[0];
		String nickName = request[1];
		String userName = request[2];
		String password = request[3];
		String inputAdminCode = request[4];
		PersistenceManager pm = PMF.getPersistenceManager();
		if (checkDuplicate(userName)) {
			return null;
		}
		boolean isAdmin = false;
		if (inputAdminCode.compareTo(adminCode) == 0) {
			isAdmin = true;
		}
		try {
		pm.makePersistent(new User(emailAddress, nickName, userName, password, isAdmin));
		}
		finally {
			pm.refreshAll();
			pm.close();
		}
		LoginInfo loginInfo = new LoginInfo(emailAddress, nickName, isAdmin);
		return loginInfo;
	}
}
