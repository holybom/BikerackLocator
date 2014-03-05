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
	private ArrayList<Rack> racks = new ArrayList<Rack>();
	
	// Constructor, for sending racks to client by request
	public RackServiceImpl() throws IOException {
		// TODO: implement this; currently just try to parse data from remote cs server for testing,
		// move this functionality to admin service and delete this constructor
		url = new URL(host + year + name);
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
		ArrayList<String> titleLine = new ArrayList<String>();
		// this while loop to grab the column labels and num of cols, 
		// this is overcomplicating things but, more resistant to format change of the dataset
		// can delete later
		while ((line = br.readLine()) != null) {
			rowCount++;
			if (rowCount < titleLineNum) continue;
			String[] titleLineRaw = line.split(delimiter);
			// traverse through the obtained title line and get the col labels
			// i starts from 1 because for reason, the first element is "" but still passes the if check
			for (int i = 1; i < titleLineRaw.length; i++) {
				if (titleLineRaw[i] != "") {
					colCount++;
					titleLine.add(titleLineRaw[i]);
				}
			}
			break;
		}
		// reset rowcount to 0 to "seriously" parse data now
		rowCount = 0;
		// do this to actually move to the next line in the while loop
		line = br.readLine(); 
		// parse data
		while ((line = br.readLine()) != null) {
			if (emptyLine(line)) continue;
			rowCount++;
			String[] rack = line.split(delimiter, colCount + 2);
			for (int i = 1; i < colCount + 1; i++) {
					System.out.println("Row: " + rowCount + "; Column: " + i + "; " 
										+ titleLine.get(i-1) + ": " + rack[i]);
				}
			// TODO: delete this line after done, just for testing
			if (rowCount > 5) break;
		}
		br.close();
	}
	
	private boolean emptyLine(String line) {
		String[] splitLine = line.split(delimiter);
		for (String str : splitLine) {
			if (str != "") return false;
		}
		return true;
	}

	private void checkFormatChange(String[] rack) {
		// TODO Auto-generated method stub
		
	}

	public com.codeunicorns.bikerack.client.Rack[] getRacks() {
		return null;
	}
	public String[] getTableView() {
		return null;
	}
}
