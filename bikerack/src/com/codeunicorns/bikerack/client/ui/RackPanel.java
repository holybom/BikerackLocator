package com.codeunicorns.bikerack.client.ui;

import java.util.Arrays;
import java.util.List;

import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Simple rack list or table view on the left side of the map (west of mainPanel),  
 * NOTE: uses TabLayout for future implementation of Searching
 */
public class RackPanel extends VerticalPanel {
	private static RackPanel panelInstance;
	private Label rackPanelLabel = new Label("BIKE RACKS");	
	private PushButton toggleMarkersButton = new PushButton("Toggle markers overlay");
	private PushButton showFavoritesButton = new PushButton("Show favorite racks");
	private boolean areMarkersVisible = true;
	private UIController uiController;
	CellList<String> cellList = new CellList<String>(new TextCell());
	final ListDataProvider<String> dataProvider = new ListDataProvider<String>();
//	private static final List<String> DAYS = Arrays.asList("Sunday", "Monday",
//		      "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

	
	public static RackPanel getInstance(UIController uiController) {
		if (panelInstance == null) panelInstance = new RackPanel(uiController);
		return panelInstance;
	}
	
	private RackPanel(final UIController uiController) {
		// TODO Implement rack list display, only label for now
		this.uiController = uiController;
		this.add(rackPanelLabel);
		this.add(toggleMarkersButton);
		this.add(showFavoritesButton);
		dataProvider.addDataDisplay(cellList);		
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        String selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          Window.alert("You selected: " + selected);
	        }
	      }
	    });		
		
		
		this.add(cellList);
		
		toggleMarkersButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (areMarkersVisible) {
					areMarkersVisible = false;
					uiController.hideAllMarkers();
				}
				else {
					areMarkersVisible = true;
					uiController.showAllMarkers();
				}
				
			}
		});
		showFavoritesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				uiController.showFavoriteMarkers();
			}
			
		});
	}

	PushButton getShowFavoritesButton() {
		return showFavoritesButton;
	}
	
	void setRackList(Rack[] rackList) {
		String[] rackInfoList = new String[rackList.length];
		for (int i = 0; i < rackList.length; i++) {
			Rack rack = rackList[i];
			rackInfoList[i] = rack.getStreetNum() + " " + rack.getStreetName()
					+ ", \n id " + rack.getId();
		}
		List<String> list = Arrays.asList(rackInfoList);
		dataProvider.setList(list);
	}
}