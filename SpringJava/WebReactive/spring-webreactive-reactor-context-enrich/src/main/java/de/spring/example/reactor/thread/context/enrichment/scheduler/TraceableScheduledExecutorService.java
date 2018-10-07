package de.spring.example.reactor.thread.context.enrichment.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;

public class TraceableScheduledExecutorService extends TraceableExecutorService implements ScheduledExecutorService {

	public TraceableScheduledExecutorService(BeanFactory beanFactory, final ExecutorService delegate) {
		super(beanFactory, delegate);
	}

	private ScheduledExecutorService getScheduledExecutorService() {
		return (ScheduledExecutorService) this.delegate;
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Runnable r = new TraceRunnable(command);
		return getScheduledExecutorService().schedule(r, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        Callable<V> c = new TraceCallable<>(callable);
		return getScheduledExecutorService().schedule(c, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Runnable r = new TraceRunnable(command);
		return getScheduledExecutorService().scheduleAtFixedRate(r, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        Runnable r = new TraceRunnable(command);
		return getScheduledExecutorService().scheduleWithFixedDelay(r, initialDelay, delay, unit);
	}

}
