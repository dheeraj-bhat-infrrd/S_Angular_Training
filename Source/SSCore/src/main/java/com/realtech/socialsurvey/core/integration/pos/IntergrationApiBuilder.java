package com.realtech.socialsurvey.core.integration.pos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;
import com.realtech.socialsurvey.core.integration.pos.errorhandlers.EncompassHttpErrorHandler;

@Component
public class IntergrationApiBuilder implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(IntergrationApiBuilder.class);

	private EncompassIntegrationAPI encompassApi;

	@Value("${ENCOMPASS_END_POINT}")
	private String encompassEndPoint;

	public EncompassIntegrationAPI getEncompassApiHandler() {
		return encompassApi;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Initialising rest builder");
		RestAdapter encompassAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(encompassEndPoint)
				.setErrorHandler(new EncompassHttpErrorHandler()).build();
		encompassApi = encompassAdapter.create(EncompassIntegrationAPI.class);
	}

}
