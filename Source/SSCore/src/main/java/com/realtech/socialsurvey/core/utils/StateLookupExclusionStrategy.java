package com.realtech.socialsurvey.core.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.realtech.socialsurvey.core.entities.StateLookup;

public class StateLookupExclusionStrategy implements ExclusionStrategy {
	public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == StateLookup.class && f.getName().equals("codeLookups"));
    }
}
