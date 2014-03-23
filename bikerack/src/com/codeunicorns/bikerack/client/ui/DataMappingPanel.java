package com.codeunicorns.bikerack.client.ui;

import java.util.ArrayList;

import com.codeunicorns.bikerack.client.Bikerack;
import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;

/**
 * And finally, this loads the map at the center
 */
public class DataMappingPanel extends TabLayoutPanel {
private static DataMappingPanel panelInstance = null;
private LayoutPanel mapPanel = new LayoutPanel();
private ScrollPanel tableViewPanel = new ScrollPanel();
private VerticalPanel importPanel2 = new VerticalPanel();
private HorizontalPanel importPanel = new HorizontalPanel();
private PushButton setURLButton = new PushButton("Set URL");
private PushButton importButton = new PushButton("Import");
private PushButton getTitlesButton = new PushButton("Get Titles");
private PushButton loadButton = new PushButton("Load");
private TextBox URLTextBox = new TextBox();
private GoogleMap map;
private FlexTable racksTable = new FlexTable();
private UIController uiController;
private Rack[] racks;
private ArrayList<InfoWindow> tooltips = new ArrayList<InfoWindow>();
	
	public static DataMappingPanel getInstance(UIController uiController) {
		if (panelInstance == null) panelInstance = new DataMappingPanel(uiController);
		return panelInstance;
	}
	
	private DataMappingPanel() {
		super(20, Unit.PX);
	};
	
	private DataMappingPanel(UIController uiController) {
		super(20, Unit.PX);
		this.uiController = uiController;
		racks = (Rack[]) uiController.dataRequest("racks");
		buildMapView();
		buildTableView();
		buildImportView();
	}

	/**
	 * might need to add intense markers making and loading later
	 */
	private void buildMapView() {
	    // Open a map centered on Vancouver, Canada
	    LatLng vancouverCity = LatLng.create(49.247,-123.114);
	    MapOptions mapOptions = MapOptions.create();	
	    mapOptions.setCenter(vancouverCity);
	    mapOptions.setMapTypeId(MapTypeId.ROADMAP);
	    // Add some controls for the zoom level
	    mapOptions.setZoom(11.0);
	    // finalize the map
	    //GoogleMap map = GoogleMap.create(mapPanel.getElement(),mapOptions);
	    map = GoogleMap.create(mapPanel.getElement(),mapOptions);
	    this.add(mapPanel);
//	    // Add markers, TODO: add the bike racks data here as markers
//	    LatLng[] latLngs = new LatLng[1];
//	    latLngs[0] = (vancouverCity);
	    //while (!triedGetRacks) {};
	    if (racks != null && racks.length != 0 && racks[0].getLat() < 9999 && racks[0].getLng() < 9999)	drawBikeracks(racks);
	  }
	
	/**
	 * foo
	 */
	private void buildTableView() {
		racksTable.setStyleName("table");
		racksTable.getRowFormatter().setStyleName(0, "tableHeader");
		racksTable.setText(0, 0, "St number");
		racksTable.setText(0, 1, "St Name");
		racksTable.setText(0, 2, "St Side");
		racksTable.setText(0, 3, "Skytrain");
		racksTable.setText(0, 4, "BIA");
		racksTable.setText(0, 5, "# of racks");
		racksTable.addStyleName("table");
		racksTable.getRowFormatter().addStyleName(0, "tableHeader");
		/* table is filled with data in the getRacks method */
		tableViewPanel.add(racksTable);
	    this.add(tableViewPanel);
	}
	
	/**
	 * foo
	 */
	private void buildImportView() {
		importPanel.add(URLTextBox);
		uiController.clientRequest("geturl", null);
		//importButton.setEnabled(false);
		//getTitlesButton.setEnabled(false);
		loadButton.setEnabled(false);
		importPanel.add(setURLButton);
		importPanel.add(importButton);
		//importPanel.add(getTitlesButton);
		importPanel.add(loadButton);
		importPanel2.add(importPanel);
		importPanel2.add((Widget) uiController.dataRequest("titleline"));
		//centralPanel.add(importPanel2);
		setImportButtonEvents();
	}
	
	private void setImportButtonEvents() {
		// TODO Auto-generated method stub
		setURLButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (URLTextBox.getValue().length() != 0) {
					setURLButton.setEnabled(false);
					importButton.setEnabled(false);
					loadButton.setEnabled(false);
					String[] request = {URLTextBox.getValue()};
					uiController.clientRequest("seturl", request);
				}
			}});
		importButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				setURLButton.setEnabled(false);
				importButton.setEnabled(false);
				loadButton.setEnabled(false);
				uiController.clientRequest("import", null);
			}});
		loadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setURLButton.setEnabled(false);
				importButton.setEnabled(false);
				loadButton.setEnabled(false);
				uiController.clientRequest("load", null);
			}});		
	}
	
	/**
	 * Create markers for the map, based on the received dataset from the server
	 * @param latLngs list of markers to create, might need to refactor this variable to global
	 */
	private void setRackMarker(Rack rack, LatLng latLng) {
		MarkerOptions markerOptions = MarkerOptions.create();
	    markerOptions.setPosition(latLng);
	    markerOptions.setMap(map);
	    final Marker marker = Marker.create(markerOptions);
	    final InfoWindow tooltip = createTooltip(rack, latLng);
	    if (!tooltips.contains(tooltip)) tooltips.add(tooltip);
	    marker.addClickListener(new Marker.ClickHandler() {
		      @Override
		      public void handle(MouseEvent event) {
		    	  for (InfoWindow tooltip : tooltips) tooltip.close();
		    	  tooltip.open(map, marker);
		    }
	    });		
	}

	private InfoWindow createTooltip(Rack rack, LatLng latlng) {
	    InfoWindowOptions tooltipOpt = InfoWindowOptions.create();
	    tooltipOpt.setPosition(latlng);
	    //tooltipOpt.setPixelOffset(Size.create(30, 20));
	    String ttSkytrain = "";
	    String ttBIA = "";
	    if (rack.getSkytrain().length() != 0) ttSkytrain = "At " + rack.getSkytrain() + " Skytrain station.";
	    if (rack.getbIA().length() != 0) ttBIA = "<p>Business Improvement Associations (initials): " + rack.getbIA() + ".</p>";
	    String address = "<p><b>Address:</b> " + rack.getStreetNum() + " " + rack.getStreetName() 
	    					+ " " + ttSkytrain + "</p>"; 
	    String noRacks = "<p><b>Number of Racks:</b> " + rack.getNumRacks() + ".</p>";
	    String allContents = "<div id=\"content\">"
			      + "<div id=\"siteNotice\">"
			      + "</div>"
			      //+ "<h1 id=\"firstHeading\" class=\"firstHeading\">Uluru</h1>"
			      + "<div id=\"bodyContent\">"
			      + address
			      + ttBIA
			      + noRacks
			      + "</div>"
			      + "</div>";
	    tooltipOpt.setContent(allContents);
	    InfoWindow tooltip = InfoWindow.create();
	    tooltip.setOptions(tooltipOpt);
	    return tooltip;
	}

	/**
	 * Accepts request from UIController to modify the ui 
	 * @param racks 
	 */
	void drawBikeracks(Rack[] racks) {
		if (racks == null) return;
		this.racks = racks;
		for (Rack rack : racks) {
			setRackMarker(rack, LatLng.create(rack.getLat(), rack.getLng()));
		}
	}

	void rebuildTableView(Rack[] racks) {
		int row = 1;
		if (racks == null || racks.length == 0 || racks[0] == null) return;
		this.racks = racks;
		for (Rack rack : racks) {
			racksTable.setText(row, 0, Integer.toString(rack.getStreetNum()));
			racksTable.setText(row, 1, rack.getStreetName());
			racksTable.setText(row, 2, rack.getStreetSide());
			racksTable.setText(row, 3, rack.getSkytrain());
			racksTable.setText(row, 4, rack.getbIA());
			racksTable.setText(row, 5, Integer.toString(rack.getNumRacks()));
			racksTable.getRowFormatter().setStyleName(row, "tableContents");
			row++;
		}
		for (int i = racksTable.getRowCount()-1; i >= row; i--) {
			racksTable.removeRow(i);
		}
		racksTable.setText(row,0, "Total");
		racksTable.setText(row,1, Integer.toString(racks.length));
		racksTable.getRowFormatter().setStyleName(row, "tableContents");	
	}

	void setURLButton(boolean enable) {
		setURLButton.setEnabled(enable);
	}

	void setImportButton(boolean enable) {
		importButton.setEnabled(enable);
	}

	void setLoadButton(boolean enable) {
		loadButton.setEnabled(enable);
	}

	void setURLTextBox(String text) {
		URLTextBox.setText(text);
	}

	HorizontalPanel getImportPanel() {
		return importPanel;
	}	
}
