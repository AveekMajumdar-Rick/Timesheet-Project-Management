package com.amazonaws.lambda.admin_operation_handler.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.lambda.admin_operation_handler.modal.ItemS3Details;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;

import software.amazon.awssdk.utils.CollectionUtils;

public class AmazonS3utils {

	public static JSONObject getObjectDetails(List<ItemS3Details> itemS3Details, LambdaLogger logger,
			JSONObject response) {
		// Split s3 Url :
		// https://ilc-timesheet-upload-dev.s3.ap-south-1.amazonaws.com/Aveek%2BMajumdar_000Y50_ILC_05-08-2022.png
		AtomicInteger index = new AtomicInteger(1);
		if (!CollectionUtils.isNullOrEmpty(itemS3Details)) {
			itemS3Details.stream().forEach(items -> {
				String[] objectKeys = items.getObjectUrl().split("//");
				String s3BucketandKeys = objectKeys[1];
				String[] keys = s3BucketandKeys.split("/");
				String[] s3bucketName = keys[0].split("\\.");
				String bucketName = s3bucketName[0];
				String objectKey = decodeValue(keys[1]).replace("+", " ");
				AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
						.withCredentials(new AWSStaticCredentialsProvider(SecretManagerDetails.getCredentials(logger)))
						.withRegion(Regions.AP_SOUTH_1).build();
				try {
					S3Object s3object = s3Client.getObject(bucketName, objectKey);
					S3ObjectInputStream inputStream = s3object.getObjectContent();
					byte[] contentInBytes = IOUtils.toByteArray(inputStream);
					String encodedContent = Base64.encodeAsString(contentInBytes);
					response.put("fileContent".concat("_" + index.getAndIncrement()), encodedContent);
					response.put("fileName", objectKey);
					response.put("uploadDateTime", items.getDate().toString("dd-MM-yyyy"));
				} catch (IOException | ConcurrentModificationException e) {
					logger.log("Unable to convert content to Byte Array: " + e);
				} catch (SdkClientException ex) {
					logger.log("Unable to fetch records from S3 bucket: " + ex);
				}
			});
		} else {
			logger.log("empty items");
		}
		return response;
	}

	public static String decodeValue(String value) {
		String decodedValue = null;
		try {
			decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			ex.getMessage();
		}
		return decodedValue;
	}
}
