package com.codeunicorns.bikerack.client;

import com.google.gwt.user.client.Timer;

/**
 * An object that can loop the calling process for the indicated amount of time. 
 * Beware of hanging when used
 */
public class DelayLock {
	private boolean lock;
	private short delay;
	
	public DelayLock() {
		lock = false;
	};
	
	/**
	 * start delaying the calling process for the indicated time, maximum 32,767 seconds
	 * @param delay delay time in seconds
	 */
	public void start(short delay) {
		this.delay = delay;
		lock = true;
		Timer delayTimer = new Timer() {
			@Override
			public void run() {
				lock = false;
				this.cancel();
			}
		};
		delayTimer.scheduleRepeating(delay*1000);
		while (lock) {};
	}
}
