package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.services.organizationmanagement.UtilityService;

@Component
public class UtilityServiceImpl implements UtilityService{
	
	@Autowired
	private GenericDao<VerticalsMaster, Integer> verticalMastersDao;
	
	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;
	
	private static final Logger LOG = LoggerFactory.getLogger(UtilityServiceImpl.class);
	
	/**
	 * Method to populate the vertical master map
	 */
	@Override
	@Transactional
	public Map<Integer, VerticalsMaster> populateVerticalMastersMap() {
		LOG.info("Method called to populate vertical masters table");
		Map<Integer, VerticalsMaster> verticalsMastersMap = new HashMap<>();
		List<VerticalsMaster> verticalsMasters = verticalMastersDao.findAllActive(VerticalsMaster.class);
		if (verticalsMasters != null && !verticalsMasters.isEmpty()) {
			for (VerticalsMaster verticalsMaster : verticalsMasters) {
				verticalsMastersMap.put(verticalsMaster.getVerticalsMasterId(), verticalsMaster);
			}
		}
		return verticalsMastersMap;
	}
	
	/**
	 * Method to fetch profile masters from db and store in the map
	 */
	@Override
	@Transactional
	public Map<Integer, ProfilesMaster> populateProfileMastersMap() {
		Map<Integer, ProfilesMaster> profileMasters = new HashMap<Integer, ProfilesMaster>();
		List<ProfilesMaster> profileMasterList = profilesMasterDao.findAllActive(ProfilesMaster.class);
		if (profileMasterList != null && !profileMasterList.isEmpty()) {
			for (ProfilesMaster profilesMaster : profileMasterList) {
				profileMasters.put(profilesMaster.getProfileId(), profilesMaster);
			}
		}
		else {
			LOG.warn("No profile master found in database");
		}
		LOG.debug("Successfully populated profile masters from database into map");
		return profileMasters;
	}

}
