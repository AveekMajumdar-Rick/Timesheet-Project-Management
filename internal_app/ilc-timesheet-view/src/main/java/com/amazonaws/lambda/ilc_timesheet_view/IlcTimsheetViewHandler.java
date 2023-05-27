package com.amazonaws.lambda.ilc_timesheet_view;

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
import com.amazonaws.lambda.ilc_timesheet_view.DbTransaction.DBOperation;
import com.amazonaws.lambda.ilc_timesheet_view.model.Item;
import com.amazonaws.lambda.ilc_timesheet_view.model.ItemS3Details;
import com.amazonaws.lambda.ilc_timesheet_view.utils.AmazonS3utils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusFamily;

public class IlcTimsheetViewHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();

		JSONObject responseJson = new JSONObject();
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
				if (item != null) {
					List<ItemS3Details> itemS3Details = DBOperation.getILCTimesheetDetails(item, logger);
					responseJson = AmazonS3utils.getObjectDetails(itemS3Details, logger, responseJson);
					responseJson.put("employeeId", item.getEmployeeId());
					responseJson.put("employeeName", item.getEmployeeName());
				}

			}

		} catch (SdkClientException ex) {
			logger.log("error in Admin operations: " + ex);
			responseJson.put("message", ex.getMessage());
			responseJson.put("status", HttpStatusFamily.SERVER_ERROR);
		} catch (ParseException e) {
			logger.log("Parsing error: " + e);
			responseJson.put("message", e.getMessage());
			responseJson.put("status", HttpStatusFamily.SERVER_ERROR);
		} catch (SQLException e) {
			logger.log("DB error: " + e);
			responseJson.put("message", e.getMessage());
			responseJson.put("status", HttpStatusFamily.SERVER_ERROR);
		}

		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();
	}

}
