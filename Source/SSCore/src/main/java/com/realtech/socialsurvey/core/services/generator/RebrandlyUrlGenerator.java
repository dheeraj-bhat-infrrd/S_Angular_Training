package com.realtech.socialsurvey.core.services.generator;

import com.realtech.socialsurvey.core.vo.RebrandlyVO;

public interface RebrandlyUrlGenerator {

	public RebrandlyVO getShortenedUrl( String surveyUrl );
}
