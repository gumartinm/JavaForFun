package de.example.sql.deadlocks.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException;

import de.example.sql.deadlocks.annotation.DeadlockRetry;

/**
 * Following example from: http://java-success.blogspot.com.es/2013/08/deadlock-retry-with-spring-aop-using.html
 *
 */
@Aspect
public class DeadlockRetryAspect implements Ordered {
	private static final Logger logger = LoggerFactory.getLogger(DeadlockRetryAspect.class);
	private static final int ORDER = 99;

	@Around(value = "@annotation(deadlockRetry)", argNames = "deadlockRetry")
	public Object doAround(final ProceedingJoinPoint pjp, final DeadlockRetry deadlockRetry) throws Throwable {

		final int maxTries = deadlockRetry.maxTries();
        final long interval = deadlockRetry.interval();

        final Object target = pjp.getTarget();
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        final Method method = signature.getMethod();
        
        int count = 0;
        Throwable deadLockException = null;
    	do {
    		try {
    			count++;

    			logger.info("Attempting to invoke method " + method.getName() + " on " + target.getClass() + " count " + count);
    			//Calling real method
    			Object result = pjp.proceed();
    			logger.info("Completed invocation of method " + method.getName() + " on " + target.getClass());

    			return result;
    		} catch (final Throwable e1) {

    			if (!isDeadLock(e1)) {
    				throw e1;
    			}

    			deadLockException = e1;
    			if (interval > 0) {
    				try {
    					Thread.sleep(interval);
    				} catch (final InterruptedException e2) {
    					logger.warn("Deadlock retry thread interrupt", e2);
                	
    					// Restore interrupt status.
    					Thread.currentThread().interrupt();
    				}
    			}
    		}
    	} while (count < maxTries);

    	throw new RuntimeException("DeadlockRetry failed, deadlock in all retry attempts.", deadLockException);
    }

	@Override
	public int getOrder() {
		return ORDER;
	}

	private boolean isDeadLock(Throwable ex) {
		do {
			if (ex instanceof MySQLTransactionRollbackException) {
				return true;
			}
		} while ((ex = ex.getCause()) != null);
		
		return false;
	}
}
