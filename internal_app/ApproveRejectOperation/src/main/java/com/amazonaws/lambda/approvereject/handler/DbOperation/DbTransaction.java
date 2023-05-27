package com.amazonaws.lambda.approvereject.handler.DbOperation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;

import com.amazonaws.lambda.approvereject.handler.constants.AWSContants;
import com.amazonaws.lambda.approvereject.handler.model.Item;
import com.amazonaws.lambda.approvereject.handler.utils.SecretManagerDetails;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import software.amazon.awssdk.regions.Region;

public class DbTransaction {

	public static JSONObject update(Item item, LambdaLogger logger) throws SQLException {
		logger.log("start updating request ");
		JSONObject response = new JSONObject();
		Connection conn = null;
		try {
			// create a mysql database connection
			conn = getConnection(logger);

			String query = DBQueries.ILC_TIMESHEET_APPROVE_REJECT_QUERY;

			logger.log("DB Query: " + query);
			// create the mysql insert preparedstatement
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, item.isItemFlag() ? 1 : 2);
			preparedStatement.setString(2, item.getItemRejectReason());
			preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
			preparedStatement.setString(4, item.getApprovedRejectBy());
			preparedStatement.setString(5, item.getEmployeeId());
			preparedStatement.setString(6, item.getItemType());
			preparedStatement.setDate(7, Date.valueOf(
					LocalDate.parse(item.getItemUploadedDateTime(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))));

			// execute the preparedstatement
			preparedStatement.execute();
			response.put("statusCode", 200);
			response.put("message", "Data updated successfully an email will be sent to the Employee with "
					+ item.getEmployeeEmail() + " email id");
			response.put("employeeId", item.getEmployeeId());
		} catch (Exception e) {
			logger.log("error while saving into DB :" + e.getMessage());
			response.put("statusCode", 500);
			response.put("message", e.getMessage());
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}

		return response;
	}

	public static Connection getConnection(LambdaLogger logger) throws ClassNotFoundException, SQLException {
		String myDriver = SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1,
				AWSContants.DRIVER_CLASS_NAME);
		String myUrl = "jdbc:mysql://"
				+ SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.RDS_HOSTNAME) + "/"
				+ SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.RDS_DB_NAME);
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl,
				SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.RDS_USERNAME),
				SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.RDS_PASSWORD));
		return conn;
	}

}
