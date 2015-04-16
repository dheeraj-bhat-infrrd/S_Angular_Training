package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * LinkedIn picture url
 */
public class PictureUrls {

	private int _total;
	private List<String> values;

	public int get_total() {
		return _total;
	}

	public void set_total(int _total) {
		this._total = _total;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
