package com.amazonaws.lambda.approvereject.handler.DbOperation;

public interface DBQueries {

	public static final String ILC_TIMESHEET_APPROVE_REJECT_QUERY = "update ilc_timesheet_details set status=?,comments=?,approved_reject_datetime=?,approved_reject_by=? where employee_id=? and item_type=? and datediff(upload_datetime,?)=0";
}
