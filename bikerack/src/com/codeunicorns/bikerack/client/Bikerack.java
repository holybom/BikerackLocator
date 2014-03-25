package com.codeunicorns.bikerack.client;

import java.util.ArrayList;

import com.codeunicorns.bikerack.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.codeunicorns.bikerack.client.LoginInfo;
import com.codeunicorns.bikerack.client.AccountService;
import com.codeunicorns.bikerack.client.AccountServiceAsync;
import com.codeunicorns.bikerack.client.ui.UIController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style.Unit;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.Size;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.Cookies;
import com.gwtfb.sdk.JSOModel;
import com.gwtfb.sdk.FBCore;
import com.gwtfb.sdk.FBEvent;
import com.gwtfb.sdk.FBXfbml;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bikerack implements EntryPoint {
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private UIController uiController;
	private Rack[] racks;
	private ArrayList<InfoWindow> tooltips = new ArrayList<InfoWindow>();
	private String dataURL;
	private AdminServiceAsync adminService;
	private AccountServiceAsync accountService;
	private RackServiceAsync rackService;
	private FacebookService facebookService;
	private Boolean dataGeocoded = false;
	private String[] titleLine;
	private boolean geoCode = false;
	private boolean triedLoggedIn = false;
	private boolean triedGetRacks = false;
	private Geocoder geocoder = Geocoder.create();
	private int geocodeCount = 0;
	private LoginInfo loginInfo = new LoginInfo("","","",1);
	private Label titleLineLabel = new Label("");
	private Timer refreshRacks;
	private int REFRESH_INTERVAL = 120000;
	
	/**
	 * This is the entry point method. Where everything starts.
	 */
	public void onModuleLoad() {
		// initialize server and facebook services
		initServices();
		// build UI
		uiController = UIController.getInstance(this);
		// set login status of the web app to set visibility of the panels accordingly
		uiController.setLoginStatus(loginInfo);
		// get loginInfo from cookie or facebook
		getLoginInfo();
		// Get bike rack data from server periodically
		refreshRacks = new Timer() {
			@Override
			public void run() {
				getServerRacks();
			}
		};
		refreshRacks.scheduleRepeating(REFRESH_INTERVAL);
		refreshRacks.run();
	}
	
	/**
	 * DATA IN
	 * This function act as a receiver for server requests from the ui objects, through UIController
	 * This is the best design I can think of so far as UI and this class are tightly connected
	 * @param name name of the request (login, logout, register, import data, load data, set url)
	 * @param request contents of the request
	 */
	public void clientRequest(String name, String[] request) {
		if (name.compareTo("login") == 0) login(request);
		else if (name.compareTo("register") == 0) register(request);
		else if (name.compareTo("logout") == 0) logout();
		else if (name.compareTo("seturl") == 0) setDataURL(request[0]);
		else if (name.compareTo("import") == 0) importData();
		else if (name.compareTo("load") == 0) loadData();
		else if (name.compareTo("settable") == 0) setTableView();
		else if (name.compareTo("geturl") == 0) getDataURL();
	}
	
	/**
	 * DATA OUT
	 * This function also acts as a receiver, but for direct data grab from the UI
	 * @param name which data that the UI wants
	 * @return the requested data
	 */
	public Object dataRequest(String name) {
		if (name.compareTo("racks") == 0) return getRacks();
		if (name.compareTo("titleline") == 0) return getTitleLineLabel();
		return null;
	}
	
	/**
	 * Send login Info to be checked by the server, if server sends a non-null login info, then
	 * set the cookie and set status of client to be logged in with that loginInfo. Server sents back
	 * a null if info in login form is not a valid user
	 * @param is an array of string containing user and password (simple implementation for now) 
	 * to be sent to server for authentication
	 */
	private void login(final String[] loginRequest) {
		if (!checkInput(loginRequest)) return;
		accountService.login(loginRequest, new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				triedLoggedIn = true;
				handleError(error);
			}
			public void onSuccess(LoginInfo result) {
				triedLoggedIn = true;
				handleLogin(loginRequest, result);
				}
		});
	}
	
	/**
	 * Handle result of login request to server
	 * @param request the request with username and password
	 * @param result the returned user access state
	 */
	private void handleLogin(String[] request, LoginInfo result) {
		if (result == null) return;
		if (result.isLoggedIn()) {
			// set login status
			if (request.length > 1) {
				if (result.isAdmin()) adminService = GWT.create(AdminService.class);
				// set cookie
				String loginCookie = request[0] + " " + request[1];
				Cookies.setCookie("bikeracklocator", loginCookie);
			}
			//Window.alert("logged in");
			loginInfo = result;
			uiController.setLoginStatus(loginInfo);
		}	
		else if (request.length > 1) Window.alert("Invalid username or password");
	}
	
	/**
	 * Register new account process, send request to server. If successful, will perform login operation
	 * with LoginInfo sent back by server for confirmation.
	 * @param request contains {email, nickName, username, password, adminCode} or {facebookID};
	 */
	private void register(final String[] request) {
		if (request.length != 2 && !checkInput(request)) return;
		accountService.register(request, new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
		
			public void onSuccess(LoginInfo result) {
				handleRegister(request, result);
			}
		});
	}
	
	/**
	 * Return register result from server, use its type to determine different kind of success (isAdmin, isFacebook)
	 * @param result Invalid user access state, do not use it as client's official loginInfo, only use its type to make one
	 */
	private void handleRegister(String[] registerRequest, LoginInfo result) {
		if (registerRequest.length != 2) {
			if (result != null) Window.alert("Please log in using your new username and password");  	
			else Window.alert("Invalid information or Username already exists");
		}
		else {
			String[] loginRequest = {registerRequest[0]};
			login(loginRequest);
		}
	}
	
	/**
	 * 
	 * @param request is {username, password} or {email, nickName, username, password, adminCode} or {facebookID}
	 * @return whether input is valid
	 */
	private boolean checkInput(String[] request) {
		int i = 0;
		int end = request.length;
		// if request is from register form, then checks email separately and starts from 1 instead
		if (request.length == 5) {	
			if (request.length == 5 && !request[0].matches("^[0-9a-z]+(\\_[0-9a-z]+)*(\\.[0-9a-z]+(\\_[0-9a-z]+)*){0,1}\\@[0-9a-z]+\\.[0-9a-z]+$")) {
				Window.alert("Invalid email");
				return false;
			}
			i = 1;
			end = request.length - 1;
		}
		// traverse through the request array and checks every remaining textbox.
		while (i < end) {
			if (request[i] == "" && i != 4) return false; 
			if (!request[i].matches("^[0-9A-Za-z]{5,15}$")) {
				Window.alert("Input must be 5-15 alphanumeric characters");
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * logout process, remove cookie from browser and set client login status to logged-out
	 */
	private void logout() {
		loginInfo = new LoginInfo("","","",1);
		uiController.setLoginStatus(loginInfo);
		// remove cookie
		Cookies.removeCookie("bikeracklocator");
	}
	
	/**
	 * Retrieve login info from browser on first run by mean of cookie
	 */
	private void getLoginInfo() {		
		String cookieVal = Cookies.getCookie("bikeracklocator");
		if (cookieVal != null) {
			login(cookieVal.split(" "));
		}
		// Get facebook Info
		facebookService.checkLogggedOut();
	}
	

	/**
	 * @param adminService
	 */
	private void importData() {
		if (!loginInfo.isAdmin()) return;
		adminService.importData(new AsyncCallback<Boolean>() {
					public void onFailure(Throwable error) {
						uiController.importSuccessful(false);
						handleError(error);
					}

					public void onSuccess(Boolean result) {
						if (!result) {
							uiController.importSuccessful(false);
							Window.alert("Import failed, try again");
						}						
						else {
							getTitleLine();
							uiController.importSuccessful(true);
							//Window.alert("Import successful");
							
						}
					}
		});
	}


	/**
	 * 
	 */
	private void getTitleLine() {
		adminService.getTitleLine(new AsyncCallback<String[]>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(String[] result) {
				if (result != null) {
					//testLoadData(adminService);
					titleLine = result;
					titleLineLabel = new Label(result[0] + " " + result[1] + " " + result[2] + " " 
							+ result[3] + " " + result[4] + " " + result[5]);
				}
				//else Window.alert("Dataset format changed, cannot get new dataset");
					
			}
		});
	}

	/**
	 */
	private void loadData() {
		refreshRacks.cancel();
		adminService.loadData(null, new AsyncCallback<Boolean>() {
				public void onFailure(Throwable error) {
					uiController.importSuccessful(true);
					handleError(error);
				}

				public void onSuccess(Boolean result) {
					if (!result) {
					Window.alert("Parse failed, try again");
					}
					else {
						//testSetTableView(adminService);
						//Window.alert("Parse successful, wait for geocoding");
					}
					geoCode = true;
					refreshRacks.scheduleRepeating(REFRESH_INTERVAL);
					refreshRacks.run();
					//getServerRacks();
					uiController.importSuccessful(true);
				}
		});
	}

	private void geocodeServerData(final Rack[] racks) {
		if (racks == null || racks.length <= 0) {
			Window.alert("No data");
			return;
		}
		
		// Setup timer to refresh list automatically.
		final Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				if (geocodeCount < racks.length)
				geocodeNext(racks);
				else finishGeocoding(this, racks);
			}
		};
		refreshTimer.scheduleRepeating(500);
	}


	private void finishGeocoding(Timer timer, Rack[] racks) {
		timer.cancel();
		geoCode = false;
		this.racks = racks;
		//Window.alert("Geocoding successful, sending data to server, wait for Markers to be drawn");
		uiController.drawBikeracks(racks);
		if (!loginInfo.isAdmin()) return;
		adminService.setRacks(racks,new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Boolean result) {	
				if (!result) {
					Window.alert("Error sending geocodes to server");
				}
				//testGetRacks();
				else System.out.println("Geocodes sent to server");
			}
		});
	}

	private void geocodeNext(final Rack[] racks) {
		//for (final Rack rack : racks) {
		final int i = geocodeCount;
		if (i >= racks.length) return;
		if (racks[i].getLat() < 9999 && racks[i].getLng() < 9999) {
			geocodeCount++;
			return;
		}
		GeocoderRequest request = GeocoderRequest.create();
		String address = racks[i].getStreetNum() + "," + racks[i].getStreetName() + "," 
						+ racks[i].getStreetSide() + "," + "vancouver" + "," +"canada";
		request.setAddress(address);
		geocoder.geocode(request, new Callback() {
		      public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
		          if (status == GeocoderStatus.OK) {
		        	  LatLng latlong = results.get(0).getGeometry().getLocation();
		        	  racks[i].setLat(latlong.lat());
		        	  racks[i].setLng(latlong.lng());
		        	  //setRackMarker(rack, latlong);
		          }
		      }
		});
		geocodeCount++;
	}

	/**
	 * Save table view settings 
	 * @param adminService
	 */
	private void setTableView() {
		// TODO: actually implement this
		adminService.setTableView(null, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Boolean result) {	
				if (!result) {
					//Window.alert("Saving view settings failed, try again");
				}
				getServerRacks();
				//else Window.alert("Settings saved");
			}
		});
	}

	/**
	 * For testing getting rack information
	 */
	private void getServerRacks() {
		RackServiceAsync rackService = GWT.create(RackService.class);
		rackService.getRacks(new AsyncCallback<Rack[]>() {
			@Override
			public void onFailure(Throwable caught) {
				triedGetRacks = true;
				Window.alert("GetRacks: failure connecting to server");
			}

			@Override
			public void onSuccess(Rack[] result) {
				triedGetRacks = true;
				if (result == null || result.length == 0) {
					//Window.alert("No data found or error getting bike racks data from server");
				}
				else {
					System.out.println("Client: GetRacks returns: " + result.length);
					if (racks != null && racks.length > result.length) return;
					racks = result;
					uiController.rebuildTableView(racks);	
					if (racks.length != 0 && racks[0].getLat() < 9999 && racks[0].getLng() < 9999) uiController.drawBikeracks(racks);
					if (loginInfo != null && loginInfo.isAdmin() && geoCode == true) { 
						//Window.alert("Bike Racks retrieved");
							racks = new Rack[racks.length];
							geocodeServerData(result);
							//Window.alert("Wait for geocoding");
					}
					//testDrawMarker();
				}
			}
		});
	}
	
	private void getDataURL() {
		AdminServiceAsync adminService = GWT.create(AdminService.class);
		adminService.getDataURL(new AsyncCallback<String>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(String result) {
				uiController.setURLBox(result);
			}
		});
	}
	
	private void setDataURL(String URL) {
		AdminServiceAsync adminService = GWT.create(AdminService.class);
		adminService.setDataURL(URL, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				uiController.importSuccessful(false);
				handleError(error);
			}
			public void onSuccess(Boolean result) {
				if (!result) Window.alert("URL setting failed, try again");
				else {
					Window.alert("New data URL set");
				}
				uiController.importSuccessful(false);
			}
		});
	}
		
	private void initServices() {
		accountService = GWT.create(AccountService.class);
		rackService = GWT.create(RackService.class);
		facebookService = new FacebookService(this);
		adminService = null;
	}
	
	LoginInfo getClientLoginInfo() {
		return loginInfo;
	}
	
	private Rack[] getRacks() {
		return racks;
	}

	private Widget getTitleLineLabel() {
		return titleLineLabel;
	}
	
	 void setLoginStatus(LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
		uiController.setLoginStatus(loginInfo);
	}

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
	}
}

