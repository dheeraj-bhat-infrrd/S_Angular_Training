package com.realtech.socialsurvey.core.services.audit.impl;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.services.audit.Audit;


public class AuditImpl implements Audit
{

    private static final Logger LOG = LoggerFactory.getLogger( AuditImpl.class );
    private static final String DELIMITER = "|^|";
    private static final String KEY_VALUE_DELIMITER = "|~|";
    private static final String METADATA = "METADATA";


    @Override
    public void auditActions( String auditType, Map<String, String> auditParams, String... auditMetaData )
    {
        try {
            StringBuilder pattern = new StringBuilder( auditType ).append( DELIMITER );
            if(auditParams != null && auditParams.size() > 0){
                Set<String> auditParamsKeySet = auditParams.keySet();
                for(String auditParam : auditParamsKeySet){
                    pattern.append( auditParam ).append( KEY_VALUE_DELIMITER ).append( auditParams.get( auditParam ) ).append( DELIMITER );
                }
            }
            if(auditMetaData != null){
                pattern.append( METADATA ).append( KEY_VALUE_DELIMITER ).append( auditMetaData );
            }
            LOG.info( pattern.toString() );
            
        } catch ( Throwable thrw ) {
            LOG.error( "Error while auditing " + thrw.getMessage() );
        }

    }

}
