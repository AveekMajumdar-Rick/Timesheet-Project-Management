package com.amazonaws.lambda.internal_app_login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.SdkClientException;
import com.amazonaws.lambda.internal_app_login.DBtrasaction.DBOperation;
import com.amazonaws.lambda.internal_app_login.modal.LoginRequest;
import com.amazonaws.lambda.internal_app_login.modal.LoginResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusFamily;

public class InternalAppLoginHandler implements RequestStreamHandler {

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

			LoginRequest loginRequest = new ObjectMapper().readValue(event.toJSONString(), LoginRequest.class);
			if (loginRequest != null) {

				// decoding employee password
				String passwordDecode = new String(Base64.getDecoder().decode(loginRequest.getEmployeePassword()));
				
				logger.log("password:" +passwordDecode);
				LoginResponse loginResponse = DBOperation.getLoginDetails(loginRequest, passwordDecode, logger);

				responseJson.put("message", loginResponse.getMessage());
				if (loginResponse.getAdminFlag() != null) {
					responseJson.put("adminflag", loginResponse.getAdminFlag());

				}
			}

		} catch (

		SdkClientException ex) {
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
