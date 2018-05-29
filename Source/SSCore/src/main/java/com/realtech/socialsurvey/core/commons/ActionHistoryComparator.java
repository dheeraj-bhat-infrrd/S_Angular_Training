package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.ActionHistory;

public class ActionHistoryComparator implements Comparator<ActionHistory> {

	private static final Logger LOG = LoggerFactory.getLogger(ActionHistoryComparator.class);

	@Override
	public int compare(ActionHistory actionHistory1, ActionHistory actionHistory2) {
		LOG.debug("Comparing ActionHistory");

		if (actionHistory1.getCreatedDate() > actionHistory2.getCreatedDate()) {
			return -1;
		} else if (actionHistory1.getCreatedDate() < actionHistory2.getCreatedDate()) {
			return 1;
		} else {
			return -1;
		}
	}
}
