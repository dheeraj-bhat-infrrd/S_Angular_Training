package com.realtech.socialsurvey.core.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.realtech.socialsurvey.core.entities.ZipCodeLookup;

public class ZipCodeExclusionStrategy implements ExclusionStrategy {
	public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == ZipCodeLookup.class && f.getName().equals("stateLookup"));
    }
}