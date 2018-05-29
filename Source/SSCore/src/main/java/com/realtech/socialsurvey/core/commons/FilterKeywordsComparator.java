package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.Keyword;

public class FilterKeywordsComparator implements Comparator<Keyword> {

	private static final Logger LOG = LoggerFactory.getLogger(FilterKeywordsComparator.class);

	@Override
	public int compare(Keyword keyword1, Keyword keyword2) {
		LOG.debug("Comparing Keywords");

		if (keyword1.getModifiedOn() > keyword2.getModifiedOn()) {
			return -1;
		} else if (keyword1.getModifiedOn() < keyword2.getModifiedOn()) {
			return 1;
		} else {
			return 0;
		}
	}
}
