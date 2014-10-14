package de.example.sql.deadlocks.aspect;

import java.lang.reflect.Method;
import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import de.example.sql.deadlocks.annotation.DeadlockRetry;

/**
 * Following example from: http://java-success.blogspot.com.es/2013/08/deadlock-retry-with-spring-aop-using.html
 *
 */
@Aspect
@Order(99)
public class DeadlockRetryAspect implements Ordered {
	private static final Logger logger = LoggerFactory.getLogger(DeadlockRetryAspect.class);
	/**
	 * Whatever you want as long as it has a higher value than the one used by
	 * annotation-driven transaction-manager (it uses by default Ordered.LOWEST_PRECEDENCE)
	 */
	private static final int ORDER = 99;

	private Collection<Class<? extends Throwable>> retryableExceptionClasses;

	@Around(value = "@annotation(deadlockRetry)", argNames = "deadlockRetry")
	public Object doAround(final ProceedingJoinPoint pjp, final DeadlockRetry deadlockRetry) throws Throwable {

		final int maxTries = deadlockRetry.maxTries();
        final long interval = deadlockRetry.interval();

        final Object target = pjp.getTarget();
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        final Method method = signature.getMethod();
        
        int count = 0;
        Throwable lastException = null;
    	do {
    		try {
    			count++;

    			logger.info("Attempting to invoke method " + method.getName() + " on " + target.getClass() + " count " + count);
    			//Calling real method
    			final Object result = pjp.proceed();
    			logger.info("Completed invocation of method " + method.getName() + " on " + target.getClass());

    			return result;
    		} catch (final Throwable ex) {

    			if (!isDeadLock(ex)) {
    				throw ex;
    			}

    			lastException = ex;
    			if (interval > 0) {
    				try {
    					Thread.sleep(interval);
    				} catch (final InterruptedException exi) {
    					logger.warn("Deadlock retry thread interrupt", exi);
                	
    					// Restore interrupt status.
    					Thread.currentThread().interrupt();
    				}
    			}
    		}
    	} while (count < maxTries);

    	throw new RuntimeException("DeadlockRetry failed, deadlock in all retry attempts.", lastException);
    }

	@Override
	public int getOrder() {
		return ORDER;
	}

	public final void setRetryableExceptionClasses(final Collection<Class<? extends Throwable>> retryableExceptionClasses) {
		this.retryableExceptionClasses = retryableExceptionClasses;
	}

	private boolean isDeadLock(Throwable ex) {
		do {
			if (this.retryableExceptionClasses.contains(ex.getClass())) {
				return true;
			}
		} while ((ex = ex.getCause()) != null);
		
		return false;
	}
}
