package com.swivl.lyudmila.swivlloader.events;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class BaseEvent {
	public static final String EXTRA_SESSION_ID = "session_id";
	public static final double UNDEFINED_SESSION_ID = -1;
	private final double mSessionId;
	
	BaseEvent(final double sessionId) {
		this.mSessionId = sessionId;
	}
	
	public double getSessionId () {
		return this.mSessionId;
	}
	
	public static double getNewSessionId () {
		double sessionId;
        do {
            sessionId = Math.random();
        } while (sessionId == UNDEFINED_SESSION_ID);

        return sessionId;
	}
	
}
