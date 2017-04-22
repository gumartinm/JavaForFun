package de.aws.example.lambda;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.xray.AWSXRay;

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
		
		// XRay: http://docs.aws.amazon.com/lambda/latest/dg/lambda-x-ray.html
		LOGGER.info("XRay _X_AMZN_TRACE_ID: " + System.getenv("_X_AMZN_TRACE_ID"));
		LOGGER.info("XRay AWS_XRAY_CONTEXT_MISSING: " + System.getenv("AWS_XRAY_CONTEXT_MISSING"));
		LOGGER.info("XRay AWS_XRAY_DAEMON_ADDRESS: " + System.getenv("AWS_XRAY_DAEMON_ADDRESS"));

		
		// See: http://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java.html
		//      http://docs.aws.amazon.com/lambda/latest/dg/lambda-x-ray.html#java-tracing
		// YOU CAN TRACE ALMOST EVERYTHING, there are even Tomcat filters :O
		AWSXRay.createSubsegment("AWSLambdaExample-subsegment", subsegment -> {
		    // My lambada should be doing something here.
			// For example calculating the meaning of life :)
			LOGGER.info("AWSLambdaExample-subsegment: " + "I am calculating the meaning of life");

			
		});
		
		AWSXRay.createSegment("AWSLambdaExample-segment", subsegment -> {
		    // My lambada should be doing something here.
			// For example calculating the meaning of life :)
			LOGGER.info("AWSLambdaExample-segment: " + "I am calculating the meaning of life");

		});

		return new Output(input.getName(), "Eyes", "Classified");
	}

}
