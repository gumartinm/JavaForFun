package de.aws.example.lambda;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Check limits before using AWS lambda functions: http://docs.aws.amazon.com/lambda/latest/dg/limits.html
 *
 */
public class AWSLambdaExample implements RequestHandler<Input, Output> {
	private static final Logger LOGGER = Logger.getLogger(AWSLambdaExample.class);

	@Override
	public Output handleRequest(Input input, Context context) {
		LOGGER.info("context getAwsRequestId: " + context.getAwsRequestId());
		LOGGER.info("context getFunctionName: " + context.getFunctionName());
		LOGGER.info("context getFunctionVersion: " + context.getFunctionVersion());
		LOGGER.info("context getInvokedFunctionArn: " + context.getInvokedFunctionArn());
		LOGGER.info("context getLogGroupName: " + context.getLogGroupName());
		LOGGER.info("context getLogStreamName: " + context.getLogStreamName());
		LOGGER.info("context getMemoryLimitInMB: " + context.getMemoryLimitInMB());
		LOGGER.info("context getRemainingTimeInMillis: " + context.getRemainingTimeInMillis());
		LOGGER.info("context getIdentityId: " + context.getIdentity().getIdentityId());
		LOGGER.info("context getIdentityPoolId: " + context.getIdentity().getIdentityPoolId());
		
		// See: http://docs.aws.amazon.com/lambda/latest/dg/env_variables.html
		String environmentGus = System.getenv("ENVIRONMENT_GUS");
		LOGGER.info("ENVIRONMENT_GUS: " + environmentGus);

		return new Output(input.getName(), "Eyes", "Classified");
	}

}
