package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class Skills {

	private int _total;
	private List<SkillValues> values;

	public int get_total() {
		return _total;
	}

	public void set_total(int _total) {
		this._total = _total;
	}

	public List<SkillValues> getValues() {
		return values;
	}

	public void setValues(List<SkillValues> values) {
		this.values = values;
	}
}
