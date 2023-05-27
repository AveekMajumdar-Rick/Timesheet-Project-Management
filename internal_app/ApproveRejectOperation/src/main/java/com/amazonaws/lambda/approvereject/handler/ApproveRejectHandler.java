package com.amazonaws.lambda.approvereject.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.lambda.approvereject.handler.DbOperation.DbTransaction;
import com.amazonaws.lambda.approvereject.handler.model.Item;
import com.amazonaws.lambda.approvereject.handler.utils.EmailUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApproveRejectHandler implements RequestStreamHandler {

	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		logger.log("Loading internal app approve reject Lambda handler");
		JSONObject responseJson = new JSONObject();
		JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		// parse the API gateway request
		JSONObject event;
		try {
			event = (JSONObject) parser.parse(reader);
			logger.log("event details : " + event.toJSONString());

			Item item = new ObjectMapper().readValue(String.valueOf(event.toJSONString()), Item.class);
			responseJson = DbTransaction.update(item, logger);
			logger.log("response : " + responseJson);
			EmailUtils.sendEmail(item, logger);

		} catch (Exception ex) {
			logger.log("error in approve reject operations: " + ex);
			responseJson.put("message", ex.getMessage());
			responseJson.put("status", 500);
		}
		OutputStreamWriter writer;
		writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();

	}
}
