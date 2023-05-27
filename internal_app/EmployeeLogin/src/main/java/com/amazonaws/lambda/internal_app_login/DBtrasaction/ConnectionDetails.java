package com.amazonaws.lambda.internal_app_login.DBtrasaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.amazonaws.lambda.internal_app_login.constants.AWSContants;
import com.amazonaws.lambda.internal_app_login.utils.SecretManagerDetails;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import software.amazon.awssdk.regions.Region;

public class ConnectionDetails {

	public static Connection getConnection(LambdaLogger logger) throws ClassNotFoundException, SQLException {
		String myDriver = SecretManagerDetails.getSecretCredentials(
				logger, Region.AP_SOUTH_1,
				AWSContants.DRIVER_CLASS_NAME);
		String myUrl = "jdbc:mysql://"
				+ SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1,
						AWSContants.RDS_HOSTNAME)
				+ "/" + com.amazonaws.lambda.internal_app_login.utils.SecretManagerDetails.getSecretCredentials(logger,
						Region.AP_SOUTH_1, AWSContants.RDS_DB_NAME);
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl,
				SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1,
						AWSContants.RDS_USERNAME),
				SecretManagerDetails.getSecretCredentials(logger, Region.AP_SOUTH_1,
						AWSContants.RDS_PASSWORD));
		return conn;
	}

}
