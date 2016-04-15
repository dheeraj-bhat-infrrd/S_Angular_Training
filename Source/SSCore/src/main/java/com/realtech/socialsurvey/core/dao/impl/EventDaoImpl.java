package com.realtech.socialsurvey.core.dao.impl;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.EventDao;
import com.realtech.socialsurvey.core.entities.Event;


@Component ( "event")
public class EventDaoImpl extends GenericDaoImpl<Event, Long> implements EventDao
{

}
