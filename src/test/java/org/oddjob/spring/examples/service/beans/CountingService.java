package org.oddjob.spring.examples.service.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.atomic.AtomicInteger;

public class CountingService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(CountingService.class);
	private int from;

	private AtomicInteger count;

	@Override
	public void afterPropertiesSet() {

		count = new AtomicInteger(from);
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getCount() {
		if (count == null) {
			throw new IllegalStateException("Service unavailable");
		}
		int value = count.getAndIncrement();
		logger.debug("Returning {}", value);
		return value;
	}

	@Override
	public void destroy() {

		count = null;
	}
}
