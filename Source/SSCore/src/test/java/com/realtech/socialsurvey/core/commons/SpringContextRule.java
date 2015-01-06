package com.realtech.socialsurvey.core.commons;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextRule implements TestRule {

	private static final Logger LOG = LoggerFactory.getLogger(SpringContextRule.class);
	
	private static boolean isSpringContextLoaded = false;
	// array of classpath context
	private final String[] locations;
	private final Object target;
	private ConfigurableApplicationContext context;

	public SpringContextRule(String[] locations, Object target) {
		this.locations = locations;
		this.target = target;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				if (!isSpringContextLoaded) {
					loadContext();
					try {
						base.evaluate();
					}
					finally {
						closeContext();
					}
					isSpringContextLoaded = true;
				}
			}
		};
	}
	
	public void loadContext(){
		LOG.info("Loading context from location");
		context = new ClassPathXmlApplicationContext(locations);
		context.getAutowireCapableBeanFactory().autowireBean(target);
		context.start();
	}
	
	public void closeContext(){
		LOG.info("Closing context");
		context.close();
	}

}
