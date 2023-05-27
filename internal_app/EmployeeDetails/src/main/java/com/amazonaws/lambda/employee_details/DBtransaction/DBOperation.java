package com.amazonaws.lambda.employee_details.DBtransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.lambda.employee_details.modal.Employee;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DBOperation {

	public static List<Employee> getEmployeeDetails(LambdaLogger logger) throws SQLException {
		List<Employee> employeeList = new ArrayList<>();
		Connection conn = null;
		try {
			// get connection details from secrets
			conn = ConnectionDetails.getConnection(logger);
			String query = null;
			// DB Query for ILC
			query = "SELECT employee_id, employee_name, employee_emailid FROM internal_app.employee_master";

			logger.log("Db Query: " + query);
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.execute();
			// Prepare Resultset
			ResultSet resultSet = preparedStatement.getResultSet();
			if (resultSet != null) {
				while (resultSet.next()) {
					Employee employee = new Employee();
					employee.setEmployeeId(resultSet.getString("employee_id"));
					employee.setEmployeeName(resultSet.getString("employee_name"));
					employee.setEmployeeEmail(resultSet.getString("employee_emailid"));
					employeeList.add(employee);
				}
			}
			logger.log("items: " + Arrays.asList(employeeList));
		} catch (ClassNotFoundException c) {
			logger.log("Class not found Exception: " + c);
		} catch (SQLException e) {
			logger.log("Error while fetching data of ILC/timesheet: " + e);
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
		return employeeList;
	}

}
