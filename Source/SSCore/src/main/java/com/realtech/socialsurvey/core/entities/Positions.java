package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * LinkedIn positions
 *
 */
public class Positions {

	private int _total;
	private List<PositionValues> values;

	public int get_total() {
		return _total;
	}

	public void set_total(int _total) {
		this._total = _total;
	}

	public List<PositionValues> getValues() {
		return values;
	}

	public void setValues(List<PositionValues> values) {
		this.values = values;
	}
}
