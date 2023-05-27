package com.amazonaws.lambda.ilc_timesheet_view.DbTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.ilc_timesheet_view.model.Item;
import com.amazonaws.lambda.ilc_timesheet_view.model.ItemS3Details;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DBOperation {

	public static List<ItemS3Details> getILCTimesheetDetails(Item item, LambdaLogger logger) throws SQLException {
		List<ItemS3Details> items = new ArrayList<>();
		Connection conn = null;
		try {
			// get connection details from secrets
			conn = ConnectionDetails.getConnection(logger);
			String query = null;
			// DB Query for ILC
			query = "SELECT s3_url, upload_datetime, status, comments FROM internal_app.ilc_timesheet_details WHERE"
					+ " employee_id= '" + item.getEmployeeId() + "' AND " + "employee_name='" + item.getEmployeeName()
					+ "' AND item_type= '" + item.getItemType() + "' AND upload_datetime='"
					+ LocalDate.parse(item.getUploadTime(), DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "'";

			logger.log("Db Query: " + query);
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.execute();
			// Prepare Resultset
			ResultSet resultSet = preparedStatement.getResultSet();
			if (resultSet != null) {
				while (resultSet.next()) {
					ItemS3Details itemS3Details = new ItemS3Details();
					itemS3Details.setObjectUrl(resultSet.getString("s3_url"));
					itemS3Details.setDate(org.joda.time.LocalDate.fromDateFields(resultSet.getDate("upload_datetime")));
					itemS3Details.setStatus(resultSet.getInt("status"));
					itemS3Details.setComments(resultSet.getString("comments"));
					items.add(itemS3Details);
				}
			}
			logger.log("items: " + items.toArray());
		} catch (ClassNotFoundException c) {
			logger.log("Class not found Exception: " + c);
		} catch (SQLException e) {
			logger.log("Error while fetching data of ILC/timesheet: " + e);
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
		return items;
	}
}
