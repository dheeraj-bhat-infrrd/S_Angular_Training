package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.EmailObject;


public interface EmailDao extends GenericDao<EmailObject, Long>
{

    public void saveEmailObjectInDB( EmailObject emailObject );


    public List<EmailObject> findAllEmails();
    
    /**
     * Finds all the emails that need to be sent
     * @return
     */
    public List<EmailObject> findAllEmailsToBeSent();


    public void deleteEmail( EmailObject emailObject );
}
