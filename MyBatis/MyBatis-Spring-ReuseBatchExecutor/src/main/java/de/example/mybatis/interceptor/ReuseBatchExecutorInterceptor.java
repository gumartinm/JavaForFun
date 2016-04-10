package de.example.mybatis.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.example.mybatis.executor.ReuseBatchExecutor;

/**
 * Too much hacking for doing this stuff. No other way of using my own Executor when using Spring Mybatis.
 *
 */
public class ReuseBatchExecutorInterceptor implements Interceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReuseBatchExecutorInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] args = invocation.getArgs();
		Object target = invocation.getTarget();
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		Object result = target;
		
		// It avoids one branch when target is not Executor instance.
		if (!(target instanceof Executor)) {
			return result;
		}
		
		if (target instanceof CachingExecutor) {
			CachingExecutor cachingExecutor = (CachingExecutor) target;
			try {
				Field delegateField = getField(cachingExecutor.getClass(), "delegate");
				delegateField.setAccessible(true);
				Object delegatedExecutor = delegateField.get(cachingExecutor);
				Executor executor = doReuseBatchExecutor((Executor) delegatedExecutor);
				result = new CachingExecutor(executor);
			} catch (IllegalAccessException e) {
				LOGGER.error("Error: ", e);
			} catch (NoSuchFieldException e) {
				LOGGER.error("Error: ", e);
			}
			// Do not override SimpleExecutor because it is used by SelectKeyGenerator (retrieves autoincremented value
			// from database after INSERT) ReuseBatchExecutor should also work but if MyBatis wants to use SimpleExecutor
			// why do not stick with it?
		} else if (target instanceof BatchExecutor){
			result = doReuseBatchExecutor((Executor) target);
		}

		return result;
	}

	@Override
	public void setProperties(Properties properties) {
		// Nothing to do.
	}

	private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}
	
	private ReuseBatchExecutor doReuseBatchExecutor(Executor executor) {
		Configuration configuration = null;
		try {
			final Field configurationField = getField(executor.getClass(), "configuration");
			configurationField.setAccessible(true);
			configuration = (Configuration) configurationField.get(executor);
		} catch (IllegalAccessException e) {
			LOGGER.error("Error: ", e);
		} catch (NoSuchFieldException e) {
			LOGGER.error("Error: ", e);
		}

		final Transaction trx = executor.getTransaction();
		return new ReuseBatchExecutor(configuration, trx);
	}
} 