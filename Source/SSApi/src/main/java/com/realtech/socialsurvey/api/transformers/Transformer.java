package com.realtech.socialsurvey.api.transformers;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface Transformer<A, D, B>
{
    public D transformApiRequestToDomainObject( A a, Object... objects ) throws InvalidInputException;


    public B transformDomainObjectToApiResponse( D d , Object... objects );
}
