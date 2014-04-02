package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Define our own ClickEvent class for UI Widgets, because for some reason GWT doesn't allow
 * us to create a new instance of the default ClickEvent class.
 */
public class MyClickEvent extends ClickEvent {

	public MyClickEvent() {
	}

}
