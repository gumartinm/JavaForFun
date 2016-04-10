package de.example.mybatis.executor;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

public class ReuseBatchExecutor extends BaseExecutor {
	public static final int BATCH_UPDATE_RETURN_VALUE = Integer.MIN_VALUE + 1002;

	private final Map<String, Statement> statementMap = new HashMap<>();
	private final Map<String, BatchResult> batchResultMap = new HashMap<>();

	public ReuseBatchExecutor(Configuration configuration, Transaction transaction) {
		super(configuration, transaction);
	}

	public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
		final Configuration configuration = ms.getConfiguration();
		final StatementHandler handler = configuration.newStatementHandler(this, ms, parameterObject, RowBounds.DEFAULT,
				null, null);
		final Statement stmt = reuseUpdateStatement(handler, ms, parameterObject);
		handler.batch(stmt);
		return BATCH_UPDATE_RETURN_VALUE;
	}

	public <E> List<E> doQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		Statement stmt = null;
		try {
			flushStatements();
			final Configuration configuration = ms.getConfiguration();
			final StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameterObject, rowBounds,
					resultHandler, boundSql);
			stmt = reuseQueryStatement(handler, ms.getStatementLog());
			return handler.<E> query(stmt, resultHandler);
		} finally {
			closeStatement(stmt);
		}
	}

	public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {

		try {
			final List<BatchResult> results = new ArrayList<>();
			if (isRollback) {
				return Collections.emptyList();
			} else {

				long count = 0;
				for (Map.Entry<String, Statement> entry : statementMap.entrySet()) {
					final Statement stmt = entry.getValue();
					final String sql = entry.getKey();
					final BatchResult batchResult = batchResultMap.get(sql);
					if (batchResult != null) {

						try {
							batchResult.setUpdateCounts(stmt.executeBatch());
							MappedStatement ms = batchResult.getMappedStatement();
							List<Object> parameterObjects = batchResult.getParameterObjects();
							KeyGenerator keyGenerator = ms.getKeyGenerator();
							if (Jdbc3KeyGenerator.class.equals(keyGenerator.getClass())) {
								Jdbc3KeyGenerator jdbc3KeyGenerator = (Jdbc3KeyGenerator) keyGenerator;
								jdbc3KeyGenerator.processBatch(ms, stmt, parameterObjects);
							} else if (!NoKeyGenerator.class.equals(keyGenerator.getClass())) { // issue #141
								for (Object parameter : parameterObjects) {
									keyGenerator.processAfter(this, ms, stmt, parameter);
								}
							}
				        } catch (BatchUpdateException e) {
				            StringBuilder message = new StringBuilder();
				            message.append(batchResult.getMappedStatement().getId())
				            	.append(" (batch index #")
				                .append(count + 1)
				                .append(")")
				                .append(" failed.");
				            if (count > 0) {
				            	message.append(" ")
				            		.append(count)
				            		.append(" prior sub executor(s) completed successfully, but will be rolled back.");
				            }
				            
				            throw new BatchExecutorException(message.toString(), e, results, batchResult); 
				        }
						
						results.add(batchResult);
					}

					count = count + 1;
				}

				return results;
			}
		} finally {
			for (Statement stmt : statementMap.values()) {
				closeStatement(stmt);
			}
			statementMap.clear();
			batchResultMap.clear();
		}
	}

	private Statement reuseQueryStatement(StatementHandler handler, Log statementLog) throws SQLException {
		final BoundSql boundSql = handler.getBoundSql();
		final String sql = boundSql.getSql();
		final Statement stmt;
		
		if (hasStatementFor(sql)) {
			stmt = getStatement(sql);
		} else {
			final Connection connection = getConnection(statementLog);
			stmt = handler.prepare(connection);
			putStatement(sql, stmt);
		}
		
		handler.parameterize(stmt);
		
		return stmt;
	}
	
	private Statement reuseUpdateStatement(StatementHandler handler, MappedStatement ms, Object parameterObject) throws SQLException {
		final BoundSql boundSql = handler.getBoundSql();
		final String sql = boundSql.getSql();
		final Statement stmt;

		if (hasStatementFor(sql)) {
			stmt = getStatement(sql);

			final BatchResult batchResult = batchResultMap.get(sql);
			batchResult.addParameterObject(parameterObject);

		} else {
			final Connection connection = getConnection(ms.getStatementLog());
			stmt = handler.prepare(connection);

			batchResultMap.put(sql, new BatchResult(ms, sql, parameterObject));
			putStatement(sql, stmt);
		}

		handler.parameterize(stmt);
		
		return stmt;
	}

	private boolean hasStatementFor(String sql) {
		try {
			return statementMap.keySet().contains(sql) && !statementMap.get(sql).getConnection().isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	private Statement getStatement(String s) {
		return statementMap.get(s);
	}

	private void putStatement(String sql, Statement stmt) {
		statementMap.put(sql, stmt);
	}

}