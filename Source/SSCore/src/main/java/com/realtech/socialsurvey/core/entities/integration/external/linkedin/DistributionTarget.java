package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class DistributionTarget implements Serializable{

	boolean visibleToGuest;
	
	boolean connectionsOnly;

	public boolean isVisibleToGuest() {
		return visibleToGuest;
	}

	public void setVisibleToGuest(boolean visibleToGuest) {
		this.visibleToGuest = visibleToGuest;
	}

	public boolean isConnectionsOnly() {
		return connectionsOnly;
	}

	public void setConnectionsOnly(boolean connectionsOnly) {
		this.connectionsOnly = connectionsOnly;
	}
	
	
}
