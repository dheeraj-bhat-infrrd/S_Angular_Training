package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.Industry;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;


@Component
public class IndustryTransformer implements Transformer<Industry, VerticalsMaster, Industry>
{
    public VerticalsMaster transformApiRequestToDomainObject( Industry industry )
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Industry transformDomainObjectToApiResponse( VerticalsMaster industryDO )
    {
        Industry industry = new Industry();
        if ( industryDO != null ) {
            industry.setId( industryDO.getVerticalsMasterId() );
            industry.setPriorityOrder( industryDO.getPriorityOrder() );
            industry.setVertical( industryDO.getVerticalName() );
        }
        return industry;
    }


    public List<Industry> transformDomainObjectListToApiResponseList( List<VerticalsMaster> industryDOs )
    {
        List<Industry> industries = new ArrayList<Industry>();
        if ( industryDOs != null && !industryDOs.isEmpty() ) {
            for ( VerticalsMaster industryDO : industryDOs ) {
                industries.add( this.transformDomainObjectToApiResponse( industryDO ) );
            }
        }
        return industries;
    }
}
