package com.realtech.socialsurvey.api.transformers;

/**
 * @author Shipra Goyal, RareMile
 *
 */
public interface Transformer<A, D, B>
{
    public D transformApiRequestToDomainObject( A a );


    public B transformDomainObjectToApiResponse( D d );
}
