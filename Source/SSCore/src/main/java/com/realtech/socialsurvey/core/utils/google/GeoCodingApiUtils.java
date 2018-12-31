package com.realtech.socialsurvey.core.utils.google;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

@Component
public class GeoCodingApiUtils {

	private GeoApiContext getContext(String apiKey) {
		return new GeoApiContext.Builder().apiKey(apiKey).build();
	}

	public GeocodingResult getGoogleApiResults(String apiKey, String address)
			throws ApiException, InterruptedException, IOException {
		GeoApiContext context= getContext(apiKey);
		return getGoogleApiResults(context, address);
	}
	
	public LatLng getGoogleApiResultsLocation(String apiKey, String address)
			throws ApiException, InterruptedException, IOException {
		GeoApiContext context= getContext(apiKey);
		return getGoogleApiResultsLocation(context, address);
	}
	
	public String getGoogleApiResultsPlaceId(String apiKey, String address)
			throws ApiException, InterruptedException, IOException {
		GeoApiContext context= getContext(apiKey);
		return getGoogleApiResultsPlaceId(context, address);
	}
	
	private GeocodingResult getGoogleApiResults(GeoApiContext context, String address)
			throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
		if(results != null && results.length >0)
			return results[0];
		else 
			return null;
	}
	
	private LatLng getGoogleApiResultsLocation(GeoApiContext context, String address)
			throws ApiException, InterruptedException, IOException {
		GeocodingResult result = getGoogleApiResults(context, address);
		if(result == null) {
		    return null;
		}
		return result.geometry.location;
	}
	
	private String getGoogleApiResultsPlaceId(GeoApiContext context, String address)
			throws ApiException, InterruptedException, IOException {
		GeocodingResult result = getGoogleApiResults(context, address);
		if(result == null) {
            return null;
        }
		return result.placeId;
	}
	
	
}
