package com.realtech.socialsurvey.core.facade;

import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.vo.HierarchyViewVO;

/**
 * @author manish
 *
 */
public interface HierarchyFacade
{
    /**
     * @param companyId
     * @return
     * @throws NonFatalException 
     */
    public HierarchyViewVO getCompanyHierarchyView(long companyId) throws NonFatalException;
}
