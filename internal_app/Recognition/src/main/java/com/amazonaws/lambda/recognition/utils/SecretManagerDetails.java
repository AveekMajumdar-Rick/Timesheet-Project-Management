package com.amazonaws.lambda.recognition.utils;

import java.util.Map;

import com.amazonaws.lambda.recognition.constants.AWSContants;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

public class SecretManagerDetails {

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
