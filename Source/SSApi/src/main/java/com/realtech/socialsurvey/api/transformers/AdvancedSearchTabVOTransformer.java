package com.realtech.socialsurvey.api.transformers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.AdvancedSearchTabVO;

@Component
public class AdvancedSearchTabVOTransformer
		implements Transformer<AdvancedSearchTabVO, LOSearchEngine, AdvancedSearchTabVO> {

	@Override
	public LOSearchEngine transformApiRequestToDomainObject(AdvancedSearchTabVO a, Object... objects)
			throws InvalidInputException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdvancedSearchTabVO transformDomainObjectToApiResponse(LOSearchEngine loSearchEngine, Object... objects) {
		// TODO Auto-generated method stub
		AdvancedSearchTabVO advancedSearchTabVO = new AdvancedSearchTabVO();
		// the returned object[0] is list of verticals
		advancedSearchTabVO.setDistanceCriteria(loSearchEngine.getDistanceCriteria());
		advancedSearchTabVO.setProfilesCriteria(loSearchEngine.getProfilesCriteria());
		advancedSearchTabVO.setRatingCriteria(loSearchEngine.getRatingCriteria());
		advancedSearchTabVO.setReviewCriteria(loSearchEngine.getReviewCriteria());
		advancedSearchTabVO.setSortingOrder(loSearchEngine.getSortingOrder());
		advancedSearchTabVO.setVerticals((List<VerticalsMaster>) objects[0]);
		return advancedSearchTabVO;
	}

}
