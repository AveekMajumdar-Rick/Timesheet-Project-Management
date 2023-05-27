package com.amazonaws.lambda.ilc_file_handler;

import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.lambda.ilc_file_handler.utils.DbTransaction;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class FileHandler implements RequestHandler<S3Event, JSONObject> {

	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	@Override
	public JSONObject handleRequest(S3Event event, Context context) {

		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler");

		JSONObject responseJson = new JSONObject();
		// Get bucketName from S3 event
		String bucket = event.getRecords().get(0).getS3().getBucket().getName();
		logger.log("bucket name:" + bucket);
		// Get file from S3 event
		String key = event.getRecords().get(0).getS3().getObject().getKey();
		logger.log("file name: " + key);

		try {
			// get object URL
			String fileUrl = s3.getUrl(bucket, key).toExternalForm();
			logger.log("file url: " + fileUrl);
			// save To DB
			responseJson = DbTransaction.save(key, fileUrl, logger);
		} catch (SdkClientException ex) {
			logger.log("Unable to get Url: " + ex);
		} catch (SQLException e) {
			logger.log("error while saving into DB :" + e.getMessage());
		}

		return responseJson;
	}

}
