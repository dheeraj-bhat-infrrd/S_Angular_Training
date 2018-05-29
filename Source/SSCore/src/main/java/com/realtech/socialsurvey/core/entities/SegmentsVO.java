package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

public class SegmentsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SegmentsEntity segmentsEntity;
	private List<SegmentsEntity> regionDetails;
	private List<SegmentsEntity> branchDetails;

	public SegmentsEntity getSegmentsEntity() {
		return segmentsEntity;
	}

	public void setSegmentsEntity(SegmentsEntity segmentsEntity) {
		this.segmentsEntity = segmentsEntity;
	}

	public List<SegmentsEntity> getRegionDetails() {
		return regionDetails;
	}

	public void setRegionDetails(List<SegmentsEntity> regionDetails) {
		this.regionDetails = regionDetails;
	}

	public List<SegmentsEntity> getBranchDetails() {
		return branchDetails;
	}

	public void setBranchDetails(List<SegmentsEntity> branchDetails) {
		this.branchDetails = branchDetails;
	}

	@Override
	public String toString() {
		return "SegmentsVO [segmentsEntity=" + segmentsEntity + ", regionDetails=" + regionDetails + ", branchDetails="
				+ branchDetails + "]";
	}

}
