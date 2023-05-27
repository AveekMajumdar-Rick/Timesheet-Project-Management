package com.amazonaws.lambda.ilc_file_handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class FileUploadHandlerTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"foo\": \"bar\"}";

    @Test
    public void testFileUploadHandler() throws IOException {
		/*
		 * FileUploadHandler handler = new FileUploadHandler();
		 * 
		 * InputStream input = new
		 * ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());; OutputStream output =
		 * new ByteArrayOutputStream();
		 * 
		 * //handler.handleRequest(input, output, null);
		 * 
		 * // TODO: validate output here if needed. String sampleOutputString =
		 * input.toString(); System.out.println(sampleOutputString);
		 * Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
		 */
    }
}
