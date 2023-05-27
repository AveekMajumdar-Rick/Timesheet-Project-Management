package com.amazonaws.lambda.recognition.DbTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.recognition.model.Recognition;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DBOperation {

	public static List<Recognition> getRecogntionDetails(LambdaLogger logger) throws SQLException {
		List<Recognition> recognitionDetails = new ArrayList<>();
		Connection conn = null;
		try {
			// get connection details from secrets
			conn = ConnectionDetails.getConnection(logger);
			String query = null;
			// DB Query for ILC
			query = "SELECT employee_Account, employee_IBM_Notes_ID, employee_award_title, employee_award_name FROM internal_app.recognition";
			logger.log("Db Query: " + query);
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.execute();
			// Prepare Resultset
			ResultSet resultSet = preparedStatement.getResultSet();
			if (resultSet != null) {
				while (resultSet.next()) {
					Recognition recognition = new Recognition();
					recognition.setEmployee_Account(resultSet.getString("employee_Account"));
					recognition.setEmployee_IBM_Notes_ID(resultSet.getString("employee_IBM_Notes_ID"));
					recognition.setEmployee_award_title(resultSet.getString("employee_award_title"));
					recognition.setEmployee_award_name(resultSet.getString("employee_award_name"));
					recognitionDetails.add(recognition);
				}
			}
			logger.log("recognition: " + recognitionDetails.toArray());
		} catch (ClassNotFoundException c) {
			logger.log("Class not found Exception: " + c);
		} catch (SQLException e) {
			logger.log("Error while fetching data of recognition " + e);
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
		return recognitionDetails;
	}

}
