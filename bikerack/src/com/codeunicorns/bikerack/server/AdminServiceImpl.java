package com.codeunicorns.bikerack.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.codeunicorns.bikerack.client.AdminService;
import com.codeunicorns.bikerack.client.Rack;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
//import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.LatLng;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	private String host = "http://www.ugrad.cs.ubc.ca/~b4s8/";
	private int year = 2012;
	private String name = "BikeRackData.csv"; 
	private int splitLimit = 8;
	private String delimiter = ",";
	private int titleLineNum = 3;
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private URL url;
	private HttpURLConnection connection;
	private File file;
	private FileReader fis;
	private BufferedReader br;
	private LinkedList<Rack> racks = new LinkedList<Rack>();
	private LinkedList<String> params = new LinkedList<String>();
	private String[] titleLine;
	private String line;
	private Geocoder geocoder;
	
	public AdminServiceImpl() {
		// construct default URL TODO: for testing purpose, so may delete after 
		try {
			url = new URL(host + year + name);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//geocoder = Geocoder.create();
	}
	
	public Boolean setDataURL(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new Boolean(false);
		}
		return new Boolean(true);
	}
	
	public Boolean importData() {
		// TODO: do we implement the parsing here? Datastore?
		if (url == null) {
			System.out.println("Error forming connection");
			return new Boolean(false);
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP connection error");
			}
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
			if (str != "") return false;
		}
		return true;
	}

	public Boolean loadData(String[] params) {
		// parse data
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
					// TODO: handle exception
					System.out.println("Parsing failed at this line.");
				}
				// TODO: delete this line after done, just for testing
				// if (rowCount > 5) break;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return new Boolean(false);
		}
		setRacks();
		return new Boolean(true);
	}

	private void createRack(final String[] rack) {
		// TODO Auto-generated method stub
//		GeocoderRequest request = GeocoderRequest.create();
//		String address = rack[1] + "," + rack[2] + "," + rack[3] + "," + "vancouver" + "," +"canada";
//		request.setAddress(address);
//		geocoder.geocode(request, new Callback() {
//			@Override
//		      public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
//		          if (status == GeocoderStatus.OK) {
//		        	  LatLng latlong = results.get(0).getGeometry().getLocation();
		        	  Rack clientRack = new Rack(Integer.parseInt(rack[1]), rack[2], rack[3], rack[4], 
		        			  				rack[5], Integer.parseInt(rack[6]));
		        	  racks.add(clientRack);
//		          }
//		      }
//		});
	}

	public Boolean setTableView(String[] params) {
		return false;
	}

	public String[] getTitleLine() {
		return titleLine;
	}
	
	private void setRacks() {
		RackServiceImpl rsi = new RackServiceImpl();
		Rack[] clientRacks = new Rack[racks.size()];
		int i = 0;
		for (Rack rack : racks) {
			clientRacks[i] = rack;
			i++;
		}
		rsi.setRacks(clientRacks);
	}
}
