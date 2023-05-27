package com.amazonaws.lambda.internal_app_login.DBtrasaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.amazonaws.lambda.internal_app_login.modal.LoginRequest;
import com.amazonaws.lambda.internal_app_login.modal.LoginResponse;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DBOperation {

	public static LoginResponse getLoginDetails(LoginRequest request, String passwordDecode, LambdaLogger logger)
			throws SQLException {
		Connection conn = null;
		LoginResponse loginResponse = new LoginResponse();
		try {
			// get connection details from secrets
			conn = ConnectionDetails.getConnection(logger);
			String query = null;
			// DB Query for Login
			query = "SELECT employee_admin_flag from internal_app.employee_master WHERE" + " employee_id= '"
					+ request.getEmployeeId() + "' AND " + "employee_password='" + passwordDecode + "'";

			logger.log("Db Query: " + query);
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.execute();
			// Prepare Resultset
			ResultSet resultSet = preparedStatement.getResultSet();

			// resultset has value then return adminflag(boolean) and message
			if (resultSet != null) {
				while (resultSet.next()) {
					loginResponse.setAdminFlag(resultSet.getBoolean("employee_admin_flag"));
					loginResponse.setMessage("Login Successfull");
				}
			} else {
				loginResponse.setMessage("Login UnSuccessfull: Please Try Again");
			}

		} catch (ClassNotFoundException c) {
			logger.log("Class not found Exception: " + c);

		} catch (SQLException e) {
			logger.log("Error while fetching data of Login: " + e);

		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
		return loginResponse;
	}
}
