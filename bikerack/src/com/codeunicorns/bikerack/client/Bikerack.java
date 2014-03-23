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
	private Boolean dataGeocoded = false;
	private String[] titleLine;
	private boolean geoCode = false;
	//private boolean gotRacks = false;
	private boolean triedLoggedIn = false;
	private boolean triedGetRacks = false;
	private Geocoder geocoder = Geocoder.create();
	private int geocodeCount = 0;
	private FBCore fbCore;
	private FBEvent fbEvent;
	private boolean isFacebook = false;
	private LoginInfo loginInfo = null;
	private boolean isLoggedIn = false;
	private Label titleLineLabel = new Label("");
	private boolean status = true;
	private boolean cookie = true;
	private boolean xfbml = true;	
	
	/**
	 * This is the entry point method. Where everything starts.

	 */
	public void onModuleLoad() {
		// initialize server and facebook services
		initServices();
		// build UI
		uiController = UIController.getInstance(this);
		// set login status of the web app to set visibility of the panels accordingly
		uiController.setLoginStatus(getLoginInfo(), loginInfo);
		// Get bike rack data from server
		Timer refreshRacks = new Timer() {
			@Override
			public void run() {
				getServerRacks();
			}
		};
		refreshRacks.scheduleRepeating(60000);
		refreshRacks.run();
	}
	
	/**
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
		if (!checkInput(loginRequest)) {
			return;
		}
		AccountServiceAsync loginService = GWT.create(AccountService.class);
		loginService.login(loginRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						triedLoggedIn = true;
						handleError(error);
					}

					public void onSuccess(LoginInfo result) {
						triedLoggedIn = true;
							if (result != null) {
								loginInfo = new LoginInfo(result.getEmailAddress(), result.getNickname(), result.isAdmin());
								// set login status
								isLoggedIn = true;
								if (loginInfo.isAdmin()) adminService = GWT.create(AdminService.class);
								// set cookie
								String loginInfo = loginRequest[0] + " " + loginRequest[1];
								Cookies.setCookie("bikeracklocator", loginInfo);
								}
							else {
								Window.alert("Invalid username or password");
							}
							uiController.setLoginStatus(isLoggedIn, loginInfo);
						}
				});
	}
	
	/**
	 * Register new account process, send request to server. If successful, will perform login operation
	 * with LoginInfo sent back by server for confirmation.
	 * @param formRequest contains {email, nickName, username, password, adminCode};
	 */
	private void register(final String[] formRequest) {
		if (!checkInput(formRequest)) {
			return;
		}
		AccountServiceAsync loginService = GWT.create(AccountService.class);
		loginService.register(formRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}
					
					public void onSuccess(LoginInfo result) {
						if (result != null) {
							// Alert user if admin code was wrong
							if (formRequest[4] != "") {
								if (result.isAdmin()) {
									Window.alert("You have been registered as Admin");
								}
								else Window.alert("Invalid admin code, you have been registered as a regular user");
							}
							Window.alert("Please log in using your new username and password");
						}
						else {
							Window.alert("Invalid information or Username already exists");
						}
					}
			});
	}
	
	/**
	 * 
	 * @param request is {username, password} or {email, nickName, username, password, adminCode}
	 * @return whether input is valid
	 */
	private boolean checkInput(String[] request) {
		int i = 0;
		// if request is from register form, then checks email separately and starts from 1 instead
		if (request.length == 5) {	
			if (request.length == 5 && !request[i].matches("^[0-9a-z]+(\\_[0-9a-z]+)*(\\.[0-9a-z]+(\\_[0-9a-z]+)*){0,1}\\@[0-9a-z]+\\.[0-9a-z]+$")) {
				Window.alert("Invalid email");
				return false;
			}
			i = 1;
		}
		// traverse through the request array and checks every remaining textbox.
		while (i < request.length) {
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
	 * logout process, remove cookie from browser and set client login status to loggedout
	 */
	private void logout() {
		loginInfo = null;
		isLoggedIn = false;
		uiController.setLoginStatus(false, loginInfo);
		// remove cookie
		Cookies.removeCookie("bikeracklocator");
	}
	
	/**
	 * Retrieve login info from browser on first run by mean of cookie
	 * @return true if already logged in, else false
	 */
	private boolean getLoginInfo() {		
		String cookieVal = Cookies.getCookie("bikeracklocator");
		if (cookieVal != null) {
		//loginInfo = null;
		//isLoggedIn = false;
		//}
		//else {
			login(cookieVal.split(" "));
			//Timer lock = new Timer();
		}
		// Get facebook Info
		fbCore.getLoginStatus(new LoginStatusCallback ());
		return isLoggedIn;
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
	 * @param adminService
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
					getServerRacks();
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
				//else Window.alert("Geocodes sent to server");
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
				Window.alert("testGetRacks: failure connecting to server");
			}

			@Override
			public void onSuccess(Rack[] result) {
				triedGetRacks = true;
				if (result == null || result.length == 0) {
					//Window.alert("No data found or error getting bike racks data from server");
				}
				else {
					if (racks != null && racks.length >= result.length) return;
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
	
	//
	// Callback used when session status is changed
	//
	class SessionChangeCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
		    // Make sure cookie is set so we can use the non async method
		    //renderHomeView ();
			Window.alert("Session changed");
		}
	}
	
	//
	// Callback used to retrieve user info when logged in
	//
	class UserCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			loginInfo = new LoginInfo("Email Not Supported",jso.get("name"),false);
		}
	}
	
	// Callback used when checking login status
	class LoginStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			//renderApp( Window.Location.getHash() );
			Window.alert("Login Status Retrieved");
			if (fbCore.getAuthResponse() != null) {
				isLoggedIn = true;
				// TODO: fake login info for facebook users for testing, need to integrate with current register system
				loginInfo = new LoginInfo("Email Not Supported", "Facebook User", false);
				fbCore.api ( "/me" , new UserCallback());
			}
			else isLoggedIn = false;
			uiController.setLoginStatus(isLoggedIn, loginInfo);
			FBXfbml.parse();
		}
	}
	
	private void initServices() {
		String APPID = "1483880728501371";
		fbCore = GWT.create(FBCore.class);
		fbEvent = GWT.create(FBEvent.class);
		fbCore.init(APPID, status, cookie, xfbml);
		// Get notified when user session is changed
		//
		SessionChangeCallback sessionChangeCallback = new SessionChangeCallback ();
		fbEvent.subscribe("auth.sessionChange",sessionChangeCallback);
		
		accountService = GWT.create(AccountService.class);
		rackService = GWT.create(RackService.class);
		adminService = null;
	}

	private Rack[] getRacks() {
		return racks;
	}

	private Widget getTitleLineLabel() {
		return titleLineLabel;
	}

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
	}


}

