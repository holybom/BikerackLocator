package com.codeunicorns.bikerack.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.codeunicorns.bikerack.client.AdminService;

@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	private String host = "http://www.ugrad.cs.ubc.ca/~b4s8/";
	private int year = 2012;
	private String name = "BikeRackData.csv"; 
	private String delimiter = ",";
	private int titleLineNum = 3;
	//private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static URL url;
	private HttpURLConnection connection;
	private BufferedReader br;
	private LinkedList<Rack> racks = new LinkedList<Rack>();
	//private LinkedList<String> params = new LinkedList<String>();
	private String[] titleLine;
	private String line;
	public AdminServiceImpl() {
		try {
			url = new URL(host + year + name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//geocoder = Geocoder.create();
	}
	
	public Boolean setDataURL(String url) {
		try {
			AdminServiceImpl.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new Boolean(false);
		}
		return new Boolean(true);
	}
	
	public Boolean importData() {
		if (url == null) {
			System.out.println("Error forming connection");
			return new Boolean(false);
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP connection error");
			}
			if (br != null) br.close();
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// initialize col and row number
			int rowCount = 0;
			int colCount = 0;
			String line;
			// storing string of titles (st number, st name, etc.)
			//ArrayList<String> titleLine = new ArrayList<String>();
			// this while loop to grab the column labels and num of cols, 
			// this is overcomplicating things but, more resistant to format change of the dataset
			// can delete later
			while ((line = br.readLine()) != null) {
				rowCount++;
				if (rowCount < titleLineNum) continue;
				String[] titleLineRaw = line.split(delimiter);
				String[] titleLine = new String[titleLineRaw.length - 1];
				// traverse through the obtained title line and get the col labels
				// i starts from 1 because for reason, the first element is "" but still passes the if check
				for (int i = 1; i < titleLineRaw.length; i++) {
					if (titleLineRaw[i] != "") {
						titleLine[colCount] = titleLineRaw[i];
						colCount++;
					}
				}
				this.titleLine = titleLine;
				break;
			}
			// do this to actually move to the next line in the while loop
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return new Boolean(false);
		}
		return new Boolean(true);
	}
	
	private boolean emptyLine(String line) {
		String[] splitLine = line.split(delimiter);
		for (String str : splitLine) {
			if (str.compareTo("") != 0) return false;
		}
		return true;
	}

	public Boolean loadData(String[] params) {
		// parse data
		int limit;
		try {
			limit = parseLoadParams(params) - 1;
		}
		catch (RuntimeException e) {
			return new Boolean(false);
		}
		racks = new LinkedList<Rack>();
		if (br == null) {
			System.out.println("Error reading from source");
			return new Boolean(false);
		}
		try {
			int rowCount = 0;
			while ((line = br.readLine()) != null) {
				if (emptyLine(line)) continue;
				rowCount++;
				String[] rack = line.split(delimiter, titleLine.length + 2);
				try {
					createRack(rack);
				} catch (NumberFormatException e) {
					System.out.println("Parsing finished.");
				}
				// TODO: delete this line after done, just for testing
				if (rowCount > limit) break;
			}
			//System.out.println("Parse: Number of Racks: " + Integer.toString(rowCount - 1));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return new Boolean(false);
		}
		return setRacks();
	}

	private int parseLoadParams(String[] params) throws RuntimeException {
		// TODO: if no params found then just parse everything, right now setting to 20
		if (params == null || params.length == 0) return 20;
		int limit = 0;
		try {
			limit = Integer.parseInt(params[0]);
		} catch (NumberFormatException e) {
			throw new RuntimeException();
		}
		return limit;
	}

	private void createRack(final String[] rack) {
	  Rack clientRack = new Rack(Integer.parseInt(rack[1]), rack[2], rack[3], rack[4], 
			  				rack[5], Integer.parseInt(rack[6]), 9999, 9999);
	  racks.add(clientRack);

	}

	public Boolean setTableView(String[] params) {
		return false;
	}

	public String[] getTitleLine() {
		return titleLine;
	}
	
	private Boolean setRacks() {
		RackServiceImpl rsi = new RackServiceImpl();
		Rack[] clientRacks = new Rack[racks.size()];
		int i = 0;
		for (Rack rack : racks) {
			clientRacks[i] = rack;
			i++;
		}
		rsi.setBypassPersistence(true);
		return new Boolean(rsi.setRacks(clientRacks));
	}

	@Override
	public String getDataURL() {
		return host + year + name;
	}

	@Override
	public Boolean setRacks(com.codeunicorns.bikerack.client.Rack[] racks) {
		AccountServiceImpl asi = new AccountServiceImpl();		
		RackServiceImpl rsi = new RackServiceImpl();
		return rsi.setRacks(asi.clientRacksToServerRacks(racks));
	}

	@Override
	public Boolean userNotify(String notification) {
		AccountServiceImpl asi = new AccountServiceImpl();
		return asi.setNotification(notification);
	}
}
