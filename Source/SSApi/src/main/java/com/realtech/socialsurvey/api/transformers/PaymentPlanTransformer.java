package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.PaymentPlan;


@Component
public class PaymentPlanTransformer
    implements Transformer<PaymentPlan, com.realtech.socialsurvey.core.entities.api.PaymentPlan, PaymentPlan>
{

    public com.realtech.socialsurvey.core.entities.api.PaymentPlan transformApiRequestToDomainObject( PaymentPlan plan )
    {
        // TODO Auto-generated method stub
        return null;
    }


    public PaymentPlan transformDomainObjectToApiResponse( com.realtech.socialsurvey.core.entities.api.PaymentPlan planDO )
    {
        PaymentPlan plan = new PaymentPlan();
        if ( planDO != null ) {
            plan.setAmount( planDO.getAmount() );
            plan.setLevel( planDO.getLevel() );
            plan.setPlanCurrency( planDO.getPlanCurrency() );
            plan.setPlanId( planDO.getPlanId() );
            plan.setPlanName( planDO.getPlanName() );
            plan.setSupportingText( planDO.getSupportingText() );
            plan.setTerms( planDO.getTerms() );
        }
        return plan;
    }


    public List<PaymentPlan> transformDomainObjectListToApiResponseList(
        List<com.realtech.socialsurvey.core.entities.api.PaymentPlan> planDOs )
    {
        List<PaymentPlan> plans = new ArrayList<PaymentPlan>();
        if ( planDOs != null && !planDOs.isEmpty() ) {
            for ( com.realtech.socialsurvey.core.entities.api.PaymentPlan planDO : planDOs ) {
                plans.add( this.transformDomainObjectToApiResponse( planDO ) );
            }
        }
        return plans;
    }

}
