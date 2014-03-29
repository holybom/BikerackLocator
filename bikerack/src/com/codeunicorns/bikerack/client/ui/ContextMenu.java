package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
//import com.google.maps.gwt.client.Marker;

public class ContextMenu extends VerticalPanel {
	MyMarker marker;
	Label name = new Label();
	GoogleMap map = null;
	PopupPanel parentMenuPanel;
	UIController uiController;
	PushButton addFavoriteButton = new PushButton("Add to your Favorites");
	PushButton deleteFavoriteButton = new PushButton("Delete from your Favorites");
	PushButton removeFromMapButton = new PushButton("Remove from Map");

	/**
	 * Create a new ContextMenu panel with add/remove favorite button and remove from map button
	 * Note: Remember to use setLinks() to add event handling to the appropriate marker, or buttons won't work
	 * Note: Use showAddFavorite() to toggle visibility of the add Favorite button. If hidden, the remove Favorite button will display instead
	 */
	public ContextMenu(PopupPanel menuPanel) {
		super();
		this.parentMenuPanel = menuPanel;
		this.add(name);
		this.add(addFavoriteButton);
		this.add(deleteFavoriteButton);
		this.add(removeFromMapButton);
		addFavoriteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!marker.isFavorite()) {
					uiController.addMarkerFavorite(marker);
					marker.setFavorite(true);
				}
				parentMenuPanel.hide();
			}
		});
		deleteFavoriteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (marker.isFavorite()) {
				uiController.removeMarkerFavorite(marker);
				marker.setFavorite(false);
				}
				parentMenuPanel.hide();
			}
		});
		removeFromMapButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				marker.getMarker().setMap(map);
				parentMenuPanel.hide();
			}
		});
		
	};
	
	/**
	 *  Add event handling to the appropriate marker
	 * @param map where the given marker is
	 * @param marker the one that is right-clicked
	 * @param the app's main UIController
	 */
	public void setLinks(MyMarker marker, UIController uiController) {
		this.marker = marker;
		this.uiController = uiController;
		name.setText(marker.getRack().getStreetNum() + " " + marker.getRack().getStreetName());
		this.add(addFavoriteButton);
		this.add(deleteFavoriteButton);
		if (marker.isFavorite()) this.remove(addFavoriteButton);
		else this.remove(deleteFavoriteButton);
	}
	
}
