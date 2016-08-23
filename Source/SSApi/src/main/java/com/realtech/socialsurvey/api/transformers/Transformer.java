package com.realtech.socialsurvey.api.transformers;


public interface Transformer<A, D, B>
{
    public D transformApiRequestToDomainObject( A a, Object... objects );


    public B transformDomainObjectToApiResponse( D d , Object... objects );
}
