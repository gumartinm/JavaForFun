package de.spring.example.reactor.thread.context.enrichment.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.BeanFactory;

import de.spring.example.reactor.thread.context.enrichment.scheduler.TraceCallable;
import de.spring.example.reactor.thread.context.enrichment.scheduler.TraceRunnable;


public class TraceableExecutorService implements ExecutorService {
	final ExecutorService delegate;
	BeanFactory beanFactory;

    public TraceableExecutorService(BeanFactory beanFactory, final ExecutorService delegate) {
		this.delegate = delegate;
		this.beanFactory = beanFactory;
	}

	@Override
	public void execute(Runnable command) {
        final Runnable r = new TraceRunnable(command);
		this.delegate.execute(r);
	}

	@Override
	public void shutdown() {
		this.delegate.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return this.delegate.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return this.delegate.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.delegate.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.delegate.awaitTermination(timeout, unit);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
        Callable<T> c = new TraceCallable<>(task);
		return this.delegate.submit(c);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
        Runnable r = new TraceRunnable(task);
		return this.delegate.submit(r, result);
	}

	@Override
	public Future<?> submit(Runnable task) {
        Runnable r = new TraceRunnable(task);
		return this.delegate.submit(r);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return this.delegate.invokeAll(wrapCallableCollection(tasks));
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.delegate.invokeAll(wrapCallableCollection(tasks), timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return this.delegate.invokeAny(wrapCallableCollection(tasks));
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return this.delegate.invokeAny(wrapCallableCollection(tasks), timeout, unit);
	}

	private <T> Collection<? extends Callable<T>> wrapCallableCollection(Collection<? extends Callable<T>> tasks) {
		List<Callable<T>> ts = new ArrayList<>();
		for (Callable<T> task : tasks) {
			if (!(task instanceof TraceCallable)) {
                ts.add(new TraceCallable<>(task));
			}
		}
		return ts;
	}
}
