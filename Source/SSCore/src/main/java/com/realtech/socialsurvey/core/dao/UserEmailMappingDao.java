package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
/**
 * 
 * @author rohit
 *
 */
public interface UserEmailMappingDao  extends GenericDao<UserEmailMapping, Long>
{

    List<UserEmailMapping> getAciveUserEmailMappingForUser( User user ) throws InvalidInputException;

}
