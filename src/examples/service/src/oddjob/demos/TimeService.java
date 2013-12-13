package oddjob.demos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeService implements Clock {

	private volatile ScheduledExecutorService executor;
	
	private volatile String cachedTime;
	
	public void start() {

		Runnable getTimeJob = new Runnable() {
			@Override
			public void run() {
				cachedTime = new SimpleDateFormat(
						"HH:mm:ss").format(new Date());
			}
		};
		getTimeJob.run();
		
		executor = Executors.newSingleThreadScheduledExecutor();
		
		executor.scheduleAtFixedRate(getTimeJob,
				0, 10, TimeUnit.SECONDS);
	}
	
	@Override
	public String getTime() {
		if (executor == null) {
			throw new IllegalStateException("Time Service unavailable");
		}
		return cachedTime;
	}
	
	public void stop() {
		executor.shutdown();
		executor = null;
	}
}
