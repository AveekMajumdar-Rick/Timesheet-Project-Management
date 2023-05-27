package com.amazonaws.lambda.approvereject.handler.utils;

import java.util.Map;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.lambda.approvereject.handler.constants.AWSContants;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

public class SecretManagerDetails {

	public static AwsCredentialsProvider getCredentials(LambdaLogger logger) {

		String accessKey = null;
		String secretKey = null;
		AwsCredentialsProvider credentials = null;
		try {

			accessKey = getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.S3_ACCESS_KEY);
			secretKey = getSecretCredentials(logger, Region.AP_SOUTH_1, AWSContants.S3_SECRET_KEY);

			credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

		} catch (SdkClientException ex) {
			logger.log("error while fetching AWS credentials: " + ex);
		}
		return credentials;
	}

	public static String getSecretCredentials(LambdaLogger logger, Region region, String secretName) {
		String secretValue = null;
		try {
			SecretsManagerClient secretsClient = SecretsManagerClient.builder().region(region).build();

			GetSecretValueRequest valueRequest = GetSecretValueRequest.builder().secretId(AWSContants.SECRETS_ARN)
					.build();

			GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);

			Map<String, String> map = new ObjectMapper().readValue(valueResponse.secretString(), Map.class);

			secretValue = map.get(secretName);

		} catch (SecretsManagerException | JsonProcessingException e) {
			logger.log("error while fetching secret: " + e);
		}

		return secretValue;
	}
}
