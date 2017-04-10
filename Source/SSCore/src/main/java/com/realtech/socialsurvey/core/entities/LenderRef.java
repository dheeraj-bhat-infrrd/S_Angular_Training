package com.realtech.socialsurvey.core.entities;

public class LenderRef {
	private String nmlsId;

	@Override
	public String toString() {
		return "LenderRef [nmlsId=" + nmlsId + "]";
	}

	public String getNmlsId() {
		return nmlsId;
	}

	public void setNmlsId(String nmlsId) {
		this.nmlsId = nmlsId;
	}
	
	
}
