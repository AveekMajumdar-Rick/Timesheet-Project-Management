package com.amazonaws.lambda.ilc_file_handler.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import com.amazonaws.lambda.ilc_file_handler.queryconstants.DBQueries;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DbTransaction {

	public static JSONObject save(String key, String fileUrl, LambdaLogger logger) throws SQLException {
		JSONObject response = new JSONObject();
		Connection conn = null;
		try {
			// create a mysql database connection
			conn = getConnection();
			
			// file name : Aveek Majumdar_000Y50_ILC_02-05-2022.jpg
			String[] employeeDetails = key.split("_");
			String employeeId = employeeDetails[1];
			String employeeName = employeeDetails[0].replace("+", " ");
			String itemType = employeeDetails[2];

			String query = DBQueries.ILC_INSERT_QUERY;

			// create the mysql insert preparedstatement
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, employeeId);
			preparedStatement.setString(2, employeeName);
			preparedStatement.setDate(3, Date.valueOf(LocalDateTime.now().toLocalDate()));
			preparedStatement.setString(4, fileUrl);
			preparedStatement.setString(5,itemType);
			preparedStatement.setBoolean(6, false);

			logger.log("DB prepared Statement :" + preparedStatement);

			// execute the preparedstatement
			preparedStatement.execute();

			response.put("statusCode", 200);
			response.put("message", "File Uploaded and data saved successfully");

		} catch (SQLException e) {
			logger.log("error while saving into DB :" + e.getMessage());
			response.put("statusCode", 500);
			response.put("message", "File Uploaded but data not saved successfully in DB");
		} catch (ClassNotFoundException e) {
			logger.log("error while inserting data in DB :" + e.getMessage());
			response.put("statusCode", 500);
			response.put("message", "File Uploaded but data not saved successfully in DB");
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}

		return response;
	}

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		String myDriver = System.getenv("DRIVER_CLASS_NAME");
		String myUrl = "jdbc:mysql://"
                + System.getenv("RDS_HOSTNAME")
                + "/"
                + System.getenv("RDS_DB_NAME");
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl, System.getenv("RDS_USERNAME"),
				System.getenv("RDS_PASSWORD"));
		return conn;
	}

}
