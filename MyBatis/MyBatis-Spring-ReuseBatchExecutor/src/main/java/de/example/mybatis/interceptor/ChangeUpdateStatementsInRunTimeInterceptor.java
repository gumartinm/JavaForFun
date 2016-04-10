package de.example.mybatis.interceptor;

import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({@Signature(
		  type= Executor.class,
		  method = "update",
		  args = {MappedStatement.class, Object.class})})
public class ChangeUpdateStatementsInRunTimeInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] args = invocation.getArgs();
		Object target = invocation.getTarget();
		
		final MappedStatement stmt = (MappedStatement) args[0];
		final Object parameter = args[1];
		final BoundSql boundSql = stmt.getBoundSql(parameter);
		final String sql = boundSql.getSql();
		
		// THIS CODE IS USELESS. I WANTED TO CHANGE IN RUN TIME THE sql CODE BUT IT IS IMPOSSIBLE
		// TO SET THE NEW sql STATEMENT TO THE CURRENT MappedStatement. Why MyBatis, whyyyyyyyyy???!!! :(
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

}
