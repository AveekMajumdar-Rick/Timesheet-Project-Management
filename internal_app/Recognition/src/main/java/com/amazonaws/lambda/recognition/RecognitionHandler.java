package com.amazonaws.lambda.recognition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.lambda.recognition.DbTransaction.DBOperation;
import com.amazonaws.lambda.recognition.model.Recognition;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import software.amazon.awssdk.http.HttpStatusFamily;

public class RecognitionHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();

		JSONObject responseJson = new JSONObject();
		JSONArray responseArray = new JSONArray();
		try {
			// Get Recognition Details
			List<Recognition> recognitionDetails = DBOperation.getRecogntionDetails(logger);
			recognitionDetails.stream().forEach(recognition -> {
				responseJson.put("employee_account", recognition.getEmployee_Account());
				responseJson.put("employee_IBM_Notes_ID", recognition.getEmployee_IBM_Notes_ID());
				responseJson.put("employee_award_title", recognition.getEmployee_award_title());
				responseJson.put("employee_award_name", recognition.getEmployee_award_name());
				responseArray.add(responseJson);
			});

		} catch (SdkClientException ex) {
			logger.log("error in Admin operations: " + ex);
			responseJson.put("message", ex.getMessage());
			responseJson.put("status", HttpStatusFamily.SERVER_ERROR);
			responseArray.add(responseJson);
		} catch (SQLException e) {
			logger.log("DB error: " + e);
			responseJson.put("message", e.getMessage());
			responseJson.put("status", HttpStatusFamily.SERVER_ERROR);
			responseArray.add(responseJson);
		}
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseArray.toString());
		writer.close();

	}

}
