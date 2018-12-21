package com.realtech.socialsurvey.api.transformers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.LOSearchContactAndDistanceVO;
import com.realtech.socialsurvey.core.vo.LOSearchRankingVO;

@Component
public class SearchEngineVOTransformer
		implements Transformer<List<LOSearchRankingVO>, List<OrganizationUnitSettings>, List<LOSearchRankingVO>> {
	
	@Autowired
	private UserDao userDao;
	
	@Resource
	@Qualifier("branch")
	private BranchDao branchDao;

	@Override
	public List<OrganizationUnitSettings> transformApiRequestToDomainObject(List<LOSearchRankingVO> a,
			Object... objects) throws InvalidInputException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LOSearchRankingVO> transformDomainObjectToApiResponse(List<OrganizationUnitSettings> unitSettings,
			Object... objects) {
		List<LOSearchRankingVO> searchRankingVOs = new ArrayList<>();
		if (unitSettings != null && !unitSettings.isEmpty()) {
			for (OrganizationUnitSettings unitSetting : unitSettings) {
				searchRankingVOs.add(createVO(unitSetting,objects[0].toString()));
			}
		}
		return searchRankingVOs;
	}

	public LOSearchRankingVO createVO(OrganizationUnitSettings unitSetting,String profile) {
		LOSearchRankingVO searchRankingVO = new LOSearchRankingVO();
		searchRankingVO.setAgentId(unitSetting.getIden());
		searchRankingVO.setName(unitSetting.getContact_details().getName());

		if (unitSetting.getContact_details() != null) {
			searchRankingVO.setLoSearchContactAndDistanceVO(createContactDetailsObj(unitSetting.getContact_details(), unitSetting.getDistanceField()));
			//setting title
			searchRankingVO.setTitle(unitSetting.getContact_details().getTitle());
		}
		//TODO : NEED TO SET SEARCH RANKING RANK
		
		//setting profile images all 3 with boolean
		//TODO :remove uneccessary image url's
		searchRankingVO.setProfileImageProcessed(unitSetting.isProfileImageProcessed());
		if(unitSetting.getProfileImageUrl() != null) {
			searchRankingVO.setProfileImageUrl(unitSetting.getProfileImageUrl());
		}
		if(unitSetting.getProfileImageUrlThumbnail() != null) {
			searchRankingVO.setProfileImageUrlThumbnail(unitSetting.getProfileImageUrlThumbnail());
		}
		if(unitSetting.getProfileImageUrlRectangularThumbnail() != null) {
			searchRankingVO.setProfileImageUrlRectangularThumbnail(unitSetting.getProfileImageUrlRectangularThumbnail());
		}
		
		//get list of nmls id's 
		if(unitSetting.getLicenses() != null && unitSetting.getLicenses().getAuthorized_in() != null) {
			searchRankingVO.setNMLS(unitSetting.getLicenses().getAuthorized_in());
		}

		//TODO : get company name
		if(profile.equals(CommonConstants.AGENT_SETTINGS_COLLECTION))
			searchRankingVO.setCompanyName(userDao.getCompanyNameForUserId(unitSetting.getIden()));
		if(profile.equals(CommonConstants.COMPANY_SETTINGS_COLLECTION))
			searchRankingVO.setCompanyName(unitSetting.getContact_details().getName());
		if(profile.equals(CommonConstants.BRANCH_SETTINGS_COLLECTION))
			searchRankingVO.setCompanyName(branchDao.getCompanyNameForBranchId(unitSetting.getIden()));
		
		//setting up rating
		if(unitSetting.getSurveyStats() != null) {
			SurveyStats surveyStats = unitSetting.getSurveyStats();
			searchRankingVO.setRating(surveyStats.getAvgScore());
			searchRankingVO.setNumberOfRecentReviews(surveyStats.getRecentSurveyCount());
			searchRankingVO.setNumberOfReviews(surveyStats.getSurveyCount());
			//TODO : CLARIFY DOUBT ON RECENT REVIEWS AND ADD
			if(surveyStats.getLatestReview() != null && !surveyStats.getLatestReview().isEmpty())
				searchRankingVO.setLatestReview(surveyStats.getLatestReview());
		}
		searchRankingVO.setProfileUrl(unitSetting.getCompleteProfileUrl());
		return searchRankingVO;
	}

	private LOSearchContactAndDistanceVO createContactDetailsObj(ContactDetailsSettings contactDetailsSettings,
			Double distanceInMiles) {
		LOSearchContactAndDistanceVO loSearchContactAndDistanceVO = new LOSearchContactAndDistanceVO();

		// distance is never null hence null check not necessary
		DecimalFormat twoDForm = new DecimalFormat("#.##"); // to get the decimal point to two decimal's
		loSearchContactAndDistanceVO.setDistance(Double.valueOf(twoDForm.format(distanceInMiles)));
		if (contactDetailsSettings.getAddress() != null)
			loSearchContactAndDistanceVO.setAddress(contactDetailsSettings.getAddress());
		if (contactDetailsSettings.getAddress1() != null)
			loSearchContactAndDistanceVO.setAddress1(contactDetailsSettings.getAddress1());
		if (contactDetailsSettings.getAddress2() != null)
			loSearchContactAndDistanceVO.setAddress2(contactDetailsSettings.getAddress2());
		if (contactDetailsSettings.getCountry() != null)
			loSearchContactAndDistanceVO.setCountry(contactDetailsSettings.getCountry());
		if (contactDetailsSettings.getState() != null)
			loSearchContactAndDistanceVO.setState(contactDetailsSettings.getState());
		if (contactDetailsSettings.getCity() != null)
			loSearchContactAndDistanceVO.setCity(contactDetailsSettings.getCity());
		if (contactDetailsSettings.getAddress() != null)
			loSearchContactAndDistanceVO.setCountryCode(contactDetailsSettings.getCountryCode());
		if (contactDetailsSettings.getAddress() != null)
			loSearchContactAndDistanceVO.setZipcode(contactDetailsSettings.getZipcode());
		if (contactDetailsSettings.getContact_numbers() != null && contactDetailsSettings.getContact_numbers().getWork() != null)
			loSearchContactAndDistanceVO.setContactNumber(contactDetailsSettings.getContact_numbers().getWork());

		return loSearchContactAndDistanceVO;
	}

}
