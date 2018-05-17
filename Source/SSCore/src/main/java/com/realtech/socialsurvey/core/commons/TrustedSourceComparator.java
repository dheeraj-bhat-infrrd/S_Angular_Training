package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.entities.SocialMonitorTrustedSource;

public class TrustedSourceComparator  implements Comparator<SocialMonitorTrustedSource> {

	private static final Logger LOG = LoggerFactory.getLogger(TrustedSourceComparator.class);

	@Override
	public int compare(SocialMonitorTrustedSource trustedSource1, SocialMonitorTrustedSource trustedSource2) {
		LOG.debug("Comparing Keywords");

		if (trustedSource1.getModifiedOn() > trustedSource2.getModifiedOn()) {
			return -1;
		} else if (trustedSource1.getModifiedOn() < trustedSource2.getModifiedOn()) {
			return 1;
		} else {
			return 0;
		}
	}
}
