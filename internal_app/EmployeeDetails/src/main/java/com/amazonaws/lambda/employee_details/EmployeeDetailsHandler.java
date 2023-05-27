package com.amazonaws.lambda.employee_details;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.lambda.employee_details.DBtransaction.DBOperation;
import com.amazonaws.lambda.employee_details.modal.Employee;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.CollectionUtils;

import software.amazon.awssdk.http.HttpStatusFamily;

public class EmployeeDetailsHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();
		JSONObject responseJson = new JSONObject();
		JSONArray responseArray = new JSONArray();
		try {

			List<Employee> employees = DBOperation.getEmployeeDetails(logger);
			if (!CollectionUtils.isNullOrEmpty(employees)) {
				employees.stream().forEach(employee -> {
					JSONObject response = new JSONObject();
					response.put("employeeId", employee.getEmployeeId());
					response.put("employeeName", employee.getEmployeeName());
					response.put("employeeEmail", employee.getEmployeeEmail());
					responseArray.add(response);
				});
			}

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
