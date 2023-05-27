package com.amazonaws.lambda.ilc_file_handler.queryconstants;

public interface DBQueries {

	public static final String ILC_INSERT_QUERY = "insert into internal_app.ilc_timesheet_details (employee_id, employee_name, upload_datetime, s3_url, item_type, status) values (?, ?, ?, ?, ?, ?)";
}
