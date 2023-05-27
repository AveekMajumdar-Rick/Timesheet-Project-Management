package com.amazonaws.lambda.approvereject.handler.utils;

import com.amazonaws.lambda.approvereject.handler.constants.AWSContants;
import com.amazonaws.lambda.approvereject.handler.model.Item;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

public class EmailUtils {

	public static void sendEmail(Item item, LambdaLogger logger) {

		SesV2Client sesv2Client = SesV2Client.builder().region(Region.AP_SOUTH_1)
				.credentialsProvider(SecretManagerDetails.getCredentials(logger)).build();
		logger.log("client: " + sesv2Client);
		Destination destination = Destination.builder().toAddresses(item.getEmployeeEmail()).build();

		Content content = Content.builder().data(getEmailBody(item)).build();

		Content sub = Content.builder().data(getSubject(item)).build();

		Body body = Body.builder().html(content).build();

		Message msg = Message.builder().subject(sub).body(body).build();

		EmailContent emailContent = EmailContent.builder().simple(msg).build();

		SendEmailRequest emailRequest = SendEmailRequest.builder().destination(destination).content(emailContent)
				.fromEmailAddress(AWSContants.ADMIN_EMAILID).build();

		logger.log("email Request: " + emailRequest);

		try {
			logger.log("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
			sesv2Client.sendEmail(emailRequest);
			logger.log("email was sent");

		} catch (SesV2Exception e) {
			logger.log("error while sending email: " + e.awsErrorDetails().errorMessage());
		}

	}

	private static String getSubject(Item item) {
		return item.getEmployeeName() + " " + item.getItemType() + " status";
	}

	private static String getEmailBody(Item item) {
		return "<html>" + "<body>" + "<h2> Dear " + item.getEmployeeName() + ",</h2>" + "<br/>" + "<br/>" + "<p> Your "
				+ item.getItemType() + " has been "
				+ (item.isItemFlag()
						? "Approved.</p>" + "<br/>" + "<br/>" + "<p> Regards," + "<br/>" + "Internal App Admin team</p>"
								+ "</body>" + "</html>"
						: "Rejected due to " + item.getItemRejectReason() + ".</p>" + "<br/>" + "<br/>" + "<p> Regards,"
								+ "<br/>" + "Internal App Admin team</p>" + "</body>" + "</html>");
	}

}
