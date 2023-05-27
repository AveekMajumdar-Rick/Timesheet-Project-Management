package com.amazonaws.lambda.admin_operation_handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.SdkClientException;
import com.amazonaws.lambda.admin_operation_handler.DBtrasaction.DBOperation;
import com.amazonaws.lambda.admin_operation_handler.constants.AWSContants;
import com.amazonaws.lambda.admin_operation_handler.modal.Item;
import com.amazonaws.lambda.admin_operation_handler.modal.ItemS3Details;
import com.amazonaws.lambda.admin_operation_handler.utils.AmazonS3utils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusFamily;

public class AdminOperationHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();

		JSONObject responseBody = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			// read the request sent by API gateway
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			// parse the API gateway request
			JSONObject event = (JSONObject) parser.parse(reader);


			if (event != null) {
				logger.log("event details : " + event.toJSONString());

				Item item = new ObjectMapper().readValue(event.toJSONString(), Item.class);

				// Get ILC/Timesheet Details
				if (item != null && !item.getItemType().equalsIgnoreCase(AWSContants.PROJECT_SHEET)) {
					List<ItemS3Details> itemS3Details = DBOperation.getILCTimesheetDetails(item, logger);
					// Process ILC/Timesheet records calling S3 bucket to get file content
					responseBody = AmazonS3utils.getObjectDetails(itemS3Details, logger, responseBody);
					responseBody.put("employeeId", item.getEmployeeId());
					responseBody.put("employeeName", item.getEmployeeName());
				}

			}

		} catch (SdkClientException ex) {
			logger.log("error in Admin operations: " + ex);
			responseBody.put("message", ex.getMessage());
			responseBody.put("status", HttpStatusFamily.SERVER_ERROR);
		} catch (ParseException e) {
			logger.log("Parsing error: " + e);
			responseBody.put("message", e.getMessage());
			responseBody.put("status", HttpStatusFamily.SERVER_ERROR);
		} catch (SQLException e) {
			logger.log("DB error: " + e);
			responseBody.put("message", e.getMessage());
			responseBody.put("status", HttpStatusFamily.SERVER_ERROR);
		}

		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseBody.toString());
		writer.close();
	}

}
